package reader;

public class PGF {
int majorVersion;
int minorVersion;
Flag[] flags;
Abstract abstr;
Concrete[] concretes;

public PGF(int _majorVersion, int _minorVersion, Flag[] _flags, Abstract _abstr, Concrete[] _concretes)
{majorVersion = _majorVersion;
 minorVersion = _minorVersion;
 flags = _flags;
 abstr = _abstr;
 concretes = _concretes;
}

public String toString()
{String ss =  "PGF : \nmajor version : "+ majorVersion + ", minor version : "+ minorVersion + "\nflags : (" ;
for (int i=0; i<flags.length; i++)
	ss+= (" " + flags[i].toString());
ss+= (")\nabstract : (" + abstr.toString() + ")\nconcretes : (");
for(int i=0;i<concretes.length;i++)
	ss+=(" " + concretes[i].toString());	
ss+=")";
return ss;}

}