package org.grammaticalframework;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.*;
import java.io.ByteArrayOutputStream;

import org.grammaticalframework.reader.*;

public class PGFBuilder {
    /* ************************************************* */
    /* Public reading functions                          */
    /* ************************************************* */
    /**
     * Reads a PGF binary from a file idenfied by filename.
     *
     * @param filename the path of the pgf file.
     */
    public static PGF fromFile(String filename)
        throws FileNotFoundException, IOException
    {
        InputStream stream = new FileInputStream(filename);
        return new PGFReader(stream).readPGF();
    }

    /**
     * Reads a pgf from an input stream
     *
     * @param inStream and InputStream to read the pgf binary from.
     */
    public static PGF fromInputStream(InputStream stream)
        throws IOException
    {
        return new PGFReader(stream).readPGF();
    }
}

class PGFReader {
    
    private DataInputStream mDataInputStream;
    
    public PGFReader(InputStream is) {
        this.mDataInputStream = new DataInputStream(is);
    }
    
    public PGF readPGF() throws IOException {
        // Reading the PGF version
        int ii[]=new int[4];
        for(int i=0;i<4;i++)
            ii[i]=mDataInputStream.read();
        // Reading the global flags
        Map<String,RLiteral> flags = getListFlag();
        // Reading the abstract
        Abstract abs = getAbstract();
        String startCat = abs.startcat();
        // Reading the concrete grammars
        Concrete[] concretes = getListConcretes(startCat);
        // builds and returns the pgf object.
        PGF pgf = new PGF(makeInt16(ii[0],ii[1]),
                          makeInt16(ii[2],ii[3]),
                          flags, abs, concretes);
        mDataInputStream.close();
        return pgf;
    }

    /**
     * This function guess the default start category from the
     * PGF flags: if the startcat flag is set then it is taken as default cat.
     * otherwise "Sentence" is taken as default category.
     */
    private String getStartCat(Map<String,RLiteral> flags) {
        RLiteral cat = flags.get("startcat");
        if (cat == null)
            return "Sentence";
        else
            return ((StringLit)cat).getValue();

    }
    /* ************************************************* */
    /* Reading abstract grammar                          */
    /* ************************************************* */
    /**
     * This function reads the part of the pgf binary corresponding to
     * the abstract grammar.
     * @param is the stream to read from.
     */
    private Abstract getAbstract() throws IOException
    {
        String name = getIdent();
        Map<String,RLiteral> flags = getListFlag();
        AbsFun[] absFuns = getListAbsFun();
        AbsCat[] absCats = getListAbsCat();
        return new Abstract(name,flags,absFuns,absCats);
    }

    private Pattern[] getListPattern() throws IOException {
        int npoz = getInteger();
        Pattern[] patts = new Pattern[npoz];
        for(int i=0; i<npoz; i++)
            patts[i]=getPattern();
        return patts;
    }

    private Eq getEq() throws IOException {
        Pattern[] patts = getListPattern();
        Expr exp = getExpr();
        return new Eq(patts,exp);
    }



    private AbsFun getAbsFun() throws IOException {
        String name = getIdent();
        Type t = getType();
        int i = getInteger();
        int maybe = mDataInputStream.read();
        if (maybe == 0) return new AbsFun(name,t,i,new Eq[0]);
        Eq[] eqs = getListEq();
        return new AbsFun(name,t,i,eqs);
    }

    private AbsCat getAbsCat() throws IOException {
        String name = getIdent();
        Hypo[] hypos = getListHypo();
        String[] strs = getListIdent();
        AbsCat abcC = new AbsCat(name,hypos, strs);
        return abcC;
    }

    private AbsFun[] getListAbsFun() throws IOException {
        int npoz = getInteger();
        AbsFun[] absFuns = new AbsFun[npoz];

        if(npoz == 0)
            return absFuns;

        else for (int i=0; i<npoz; i++)
                 absFuns[i] = getAbsFun();

        return absFuns;
    }

    private AbsCat[] getListAbsCat() throws IOException {
        int npoz = getInteger();
        AbsCat[] absCats = new AbsCat[npoz];
        if(npoz == 0)
            return absCats;
        else
            for (int i=0; i<npoz; i++)
                absCats[i] = getAbsCat();

        return absCats;
    }


    private Type getType() throws IOException {
        Hypo[] hypos = getListHypo();
        String returnCat = getIdent();
        Expr[] exprs = getListExpr();
        return new Type(hypos, returnCat, exprs);

    }

    private Hypo getHypo() throws IOException { 
       int btype = mDataInputStream.read();
        boolean b = btype == 0 ? false : true;
        String varName = getIdent();
        Type t = getType();
        Hypo hh = new Hypo (b,varName,t);
        return hh;
    }

    private Hypo[] getListHypo() throws IOException {
        int npoz = getInteger();
        Hypo[] hypos = new Hypo[npoz];
        for (int i=0; i<npoz; i++)
            hypos[i]=getHypo();
        return hypos;
    }

    private Expr[] getListExpr( ) throws IOException {
        int npoz = getInteger();
        Expr[] exprs = new Expr[npoz];
        for(int i=0; i<npoz; i++)
            exprs[i]=getExpr();
        return exprs;
    }

    private Expr getExpr( ) throws IOException {
        int sel = mDataInputStream.read();
        Expr expr = null;
        switch (sel) {
        case 0 : //lambda abstraction
            int bt = mDataInputStream.read();
            boolean btype = bt == 0 ? false : true ;
            String varName = getIdent();
            Expr e1 = getExpr();
            expr = new LambdaExp(btype,varName,e1);
            break;
        case 1 : //expression application
            Expr e11 = getExpr();
            Expr e2 = getExpr();
            expr = new AppExp(e11,e2);
            break;
        case 2 : //literal expression
            RLiteral lit = getLiteral();
            expr = new LiteralExp(lit);
            break;
        case 3 : //meta variable
            int id = getInteger();
            expr = new MetaExp(id);
            break;
        case 4 : //abstract function name
            String absFun = getIdent();
            expr = new AbsNameExp(absFun);
            break;
        case 5 : //variable
            int v = getInteger();
            expr = new VarExp(v);
            break;
        case 6 : //type annotated expression
            Expr e = getExpr();
            Type t = getType();
            expr = new TypedExp(e,t);
            break;
        case 7 : //implicit argument
            Expr ee = getExpr();
            expr = new ImplExp(ee);
            break;
        default : throw new IOException("invalid tag for expressions : "+sel);
        }
        return expr;
    }


    private Eq[] getListEq( ) throws IOException {
        int npoz = getInteger();
        Eq[] eqs = new Eq[npoz];
        for (int i=0; i<npoz;i++)
            eqs[i]=getEq();
        return eqs;
    }

    private Pattern getPattern( ) throws IOException {
        int sel = mDataInputStream.read();
        Pattern patt = null;
        switch (sel) {
        case 0 : //application pattern
            String absFun = getIdent();
            Pattern[] patts = getListPattern();
            patt = new AppPattern(absFun,patts);
            break;
        case 1 : //variable pattern
            String varName = getIdent();
           patt = new VarPattern(varName);
            break;
        case 2 : //variable as pattern
            String pVarName = getIdent();
            Pattern p = getPattern();
            patt = new VarAsPattern(pVarName,p);
            break;
        case 3 : //wild card pattern
            patt = new WildCardPattern();
            break;
        case 4 : //literal pattern
            RLiteral lit = getLiteral();
            patt = new LiteralPattern(lit);
            break;
        case 5 : //implicit argument
            Pattern pp = getPattern();
            patt = new ImpArgPattern(pp);
        case 6 : //inaccessible pattern
            Expr e = getExpr();
            patt = new InaccPattern(e);
            break;
        default : throw new IOException("invalid tag for patterns : "+sel);
        }
        return patt;
    }

    private RLiteral getLiteral( ) throws IOException {
        int sel = mDataInputStream.read();
        RLiteral ss = null;
        switch (sel) {
        case 0 :
            String str = getString();
            ss = new StringLit(str);
            break;
        case 1 :
            int i = getInteger();
            ss = new IntLit(i);
            break;
        case 2 :
            double d = mDataInputStream.readDouble();
            ss = new FloatLit(d);
            break;
        default :
            throw new IOException("Incorrect literal tag "+sel);
        }
        return ss;
    }

    /* ************************************************* */
    /* Reading concrete grammar                          */
    /* ************************************************* */
    private Concrete getConcrete(String startCat) throws IOException
    {
        String name = getIdent();
        Map<String,RLiteral> flags = getListFlag();
        // We don't use the print names, but we need to read them to skip them
        getListPrintName();
        Sequence[] seqs = getListSequence();
        CncFun[] cncFuns = getListCncFun(seqs);
        ProductionSet[] prods = getListProductionSet(cncFuns);
        Map<String, CncCat> cncCats = getListCncCat();
        int i = getInteger();
        return new Concrete(name,flags,seqs,cncFuns,prods,cncCats,i,startCat);
    }

    private Concrete[] getListConcretes(String startCat)
        throws IOException
    {
        int npoz = getInteger();
        Concrete[] concretes = new Concrete[npoz];
        if(npoz == 0) return concretes;
        else
            for (int i=0; i<npoz; i++)
                concretes[i] = getConcrete(startCat);
        return concretes;
    }

    /* ************************************************* */
    /* Reading print names                               */
    /* ************************************************* */
    // FIXME : not used, we should avoid creating the objects
    private PrintName getPrintName( ) throws IOException
    {
        String absName = getIdent();
        String printName = getString();
        return new PrintName(absName, printName);

    }

    private PrintName[] getListPrintName( )
        throws IOException
    {
        int npoz = getInteger();
        PrintName[] pnames = new PrintName[npoz];
        if(npoz == 0) return pnames;
        else
            for (int i=0; i<npoz; i++)
                pnames[i] = getPrintName();
        return pnames;
    }

    /* ************************************************* */
    /* Reading sequences                                 */
    /* ************************************************* */
    private Sequence getSequence( ) throws IOException {
        Symbol[] symbols = getListSymbol();
        return new Sequence(symbols);
    }

    private Sequence[] getListSequence( )
        throws IOException
    {
        int npoz = getInteger();
        Sequence[] seqs = new Sequence[npoz];
        for(int i=0; i<npoz; i++)
            seqs[i]=getSequence();
        return seqs;
    }

    private Symbol getSymbol( ) throws IOException {
        int sel = mDataInputStream.read();
        Symbol symb = null;
        switch (sel) {
        case 0 : //constituent argument
            int i1 = getInteger();
            int i2 = getInteger();
            symb = new ArgConstSymbol(i1,i2);
            break;
        case 1 : //constituent argument -- what is the difference ?
            int i11 = getInteger();
            int i12 = getInteger();
            symb = new ArgConstSymbol(i11,i12);
            break;
        case 2 : //sequence of tokens
            String[] strs = getListString();
            symb = new ToksSymbol(strs);
            break;
        case 3 : //alternative tokens
            String[] altstrs = getListString();
            Alternative[] as = getListAlternative();
            symb = new AlternToksSymbol(altstrs,as);
            break;
        default : throw new IOException("invalid tag for symbols : "+sel);
        }
        return symb;
    }

    private Alternative[] getListAlternative( )
        throws IOException
    {
        int npoz = getInteger();
        Alternative[] alts = new Alternative[npoz];
        for(int i=0;i<npoz;i++)
            alts[i] = getAlternative();
        return alts;
    }

    private Alternative getAlternative( )
        throws IOException
    {
        String[] s1 = getListString();
        String[] s2 = getListString();
        return new Alternative(s1,s2);
    }

    private Symbol[] getListSymbol( )
        throws IOException
    {
        int npoz = getInteger();
        Symbol[] symbols = new Symbol[npoz];
        for(int i=0; i<npoz; i++)
            symbols[i]=getSymbol();
        return symbols;
    }

    /* ************************************************* */
    /* Reading concrete functions                        */
    /* ************************************************* */
    private CncFun getCncFun(Sequence[] sequences)
        throws IOException
    {
        String name = getIdent();
        int[] sIndices = getListInteger();
        int l = sIndices.length;
        Sequence[] seqs = new Sequence[l];
        for (int i = 0 ; i < l ; i++)
            seqs[i] = sequences[sIndices[i]];
        return new CncFun(name,seqs);
    }

    private CncFun[] getListCncFun(Sequence[] sequences)
        throws IOException
    {
        int npoz = getInteger();
        CncFun[] cncFuns = new CncFun[npoz];
        for(int i=0; i<npoz; i++)
            cncFuns[i]=getCncFun(sequences);
        return cncFuns;
    }

    /* ************************************************* */
    /* Reading productions and production sets           */
    /* ************************************************* */
    /**
     * Read a production set
     * @param is is the input stream to read from
     * @param cncFuns is the list of concrete function
     */
    private ProductionSet getProductionSet(CncFun[] cncFuns)
        throws IOException
    {
        int id = getInteger();
        Production[] prods = getListProduction( id, cncFuns);
        ProductionSet ps = new ProductionSet(id,prods);
        return ps;
    }

    /**
     * Read a list of production set
     * @param is is the input stream to read from
     * @param cncFuns is the list of concrete function
     */
    private ProductionSet[] getListProductionSet(CncFun[] cncFuns)
        throws IOException
    {
        int npoz = getInteger();
        ProductionSet[] prods = new ProductionSet[npoz];
        for(int i=0; i<npoz; i++)
            prods[i]= getProductionSet(cncFuns);
        return prods;
    }

    /**
     * Read a list of production
     * @param is is the input stream to read from
     * @param leftCat is the left hand side category of this production (
     * read only once for the whole production set)
     * @param cncFuns is the list of concrete function
     */
    private Production[] getListProduction(int leftCat,
                                             CncFun[] cncFuns)
        throws IOException
    {
        int npoz = getInteger();
        Production[] prods = new Production[npoz];
        for(int i=0; i<npoz; i++)
            prods[i]=getProduction(leftCat, cncFuns);
        return prods;
    }

    /**
     * Read a production
     * @param is is the input stream to read from
     * @param leftCat is the left hand side category of this production
     *                (read only once for the whole production set)
     * @param cncFuns is the list of concrete function, used here to set the
     *                function of the production (only given by its index in
     *                the list)
     */
    private Production getProduction(int leftCat,
                                            CncFun[] cncFuns)
        throws IOException
    {
        int sel = mDataInputStream.read();
        Production prod = null;
        switch (sel) {
        case 0 : //application
            int i = getInteger();
            int[] iis = getListInteger();
            prod = new ApplProduction(leftCat, cncFuns[i],iis);
            break;
        case 1 : //coercion
            int id = getInteger();
            prod = new CoerceProduction(leftCat, id);
            break;
        default : throw new IOException("invalid tag for productions : "+sel);
        }
        return prod;
    }

    /* ************************************************* */
    /* Reading concrete categories                       */
    /* ************************************************* */
    private CncCat getCncCat( ) throws IOException
    {
        String sname = getIdent();
        int firstFId = getInteger();
        int lastFId = getInteger();
        String[] ss = getListString();
        return new CncCat(sname,firstFId,lastFId,ss);
    }

    private Map<String, CncCat> getListCncCat( ) throws IOException
    {
        int npoz = getInteger();
        Map<String, CncCat> cncCats = new HashMap<String,CncCat>();
        String name;
        int firstFID, lastFID;
        String[] ss;
        for(int i=0; i<npoz; i++) {
            name = getIdent();
            firstFID = getInteger();
            lastFID = getInteger();
            ss = getListString();
            cncCats.put(name, new CncCat(name,firstFID,lastFID,ss));
        }
        return cncCats;
    }

    /* ************************************************* */
    /* Reading flags                                     */
    /* ************************************************* */
    private Map<String,RLiteral> getListFlag( )
        throws IOException {
        int npoz = getInteger();
        Map<String,RLiteral> flags = new HashMap<String,RLiteral>();
        if (npoz == 0)
            return flags;
        for (int i=0; i<npoz; i++) {
            String ss = getIdent();
            RLiteral lit = getLiteral();
            flags.put(ss, lit);
        }
        return flags;
    }

    /* ************************************************* */
    /* Reading strings                                   */
    /* ************************************************* */
    private String getString( ) throws IOException {
        // using a byte array for efficiency
        ByteArrayOutputStream os = new java.io.ByteArrayOutputStream();
        int npoz = getInteger();
        int r ;
        for (int i=0; i<npoz; i++) {
            r = mDataInputStream.read();
            os.write((byte)r);
            if (r <= 0x7f) {}                              //lg = 0;
            else if ((r >= 0xc0) && (r <= 0xdf))
                os.write((byte)mDataInputStream.read());   //lg = 1;
            else if ((r >= 0xe0) && (r <= 0xef)) {
                os.write((byte)mDataInputStream.read());   //lg = 2;
                os.write((byte)mDataInputStream.read());
            } else if ((r >= 0xf0) && (r <= 0xf4)) {
                os.write((byte)mDataInputStream.read());   //lg = 3;
                os.write((byte)mDataInputStream.read());
                os.write((byte)mDataInputStream.read());
            } else if ((r >= 0xf8) && (r <= 0xfb)) {
                os.write((byte)mDataInputStream.read());   //lg = 4;
                os.write((byte)mDataInputStream.read());
                os.write((byte)mDataInputStream.read());
                os.write((byte)mDataInputStream.read());
            } else if ((r >= 0xfc) && (r <= 0xfd)) {
                os.write((byte)mDataInputStream.read());   //lg =5;
                os.write((byte)mDataInputStream.read());
                os.write((byte)mDataInputStream.read());
                os.write((byte)mDataInputStream.read());
                os.write((byte)mDataInputStream.read());
            } else throw new IOException("Undefined for now !!! ");
        }
        return os.toString("UTF-8"); 
    }

    private String[] getListString( )
        throws IOException
    {
        int npoz = getInteger();
        String[] strs = new String[npoz];
        if(npoz == 0)
            return strs;
        else {for (int i=0; i<npoz; i++)
                strs[i] = getString();
        }
        return strs;
    }

    /**
     * Some string (like categories identifiers) are not allowed to
     * use the full utf8 tables but only latin 1 caracters.
     * We can read them faster using this knowledge.
     **/
    private String getIdent( ) throws IOException {
        int nbChar = getInteger();
        byte[] bytes = new byte[nbChar];
        this.mDataInputStream.read(bytes);
        return new String(bytes, "ISO-8859-1");
    }

    private String[] getListIdent( )
        throws IOException
    {
        int nb = getInteger();
        String[] strs = new String[nb];
        for (int i=0; i<nb; i++)
            strs[i] = getIdent();
        return strs;
    }

    /* ************************************************* */
    /* Reading integers                                  */
    /* ************************************************* */
    private int getInteger( ) throws IOException {
        long rez = (long)mDataInputStream.read();
        if (rez <= 0x7f)
            return (int)rez;
        else {
            int ii = getInteger();
            rez = (ii <<7) | (rez & 0x7f);
            return (int)rez;
        }
    }

    private int[] getListInteger( ) throws IOException
    {
        int npoz = getInteger();
        int[] vec = new int[npoz];
        for(int i=0; i<npoz; i++)
            vec[i] = getInteger();
        return vec;
    }

    private int makeInt16(int j1, int j2) {
        int i = 0;
        i |= j1 & 0xFF;
        i <<= 8;
        i |= j2 & 0xFF;
        return i;
    }
}





