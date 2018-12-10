import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.regex.Matcher
import java.util.regex.Pattern

@GrabConfig(systemClassLoader=true)
@Grab(group='org.postgresql', module='postgresql', version='9.4-1205-jdbc42')



import groovy.sql.Sql

def dbUrl      = "jdbc:postgresql://localhost/kirk"
def dbUser     = "kirk"
def dbPassword = "kirk"
def dbDriver   = "org.postgresql.Driver"

class Z {
  static Sql sql
}

Z.sql = Sql.newInstance(dbUrl, dbUser, dbPassword, dbDriver)

Z.sql.execute('''
  drop table  if exists hs_log;
create table hs_log (
  ts    timestamp,
  level character varying(4096),
  thread character varying(4096),
  class character varying(4096),
  message character varying(34096)
);
''')

def getTs(String str) {

  //  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss,SSS");
  Instant i = Instant.parse(str.replaceAll(",",".")+"Z");
  Timestamp timestamp = new java.sql.Timestamp(i.toEpochMilli());
  return timestamp;
}

def save(entry) {
  //  println(sql);
  def params = entry;//[10, 'Groovy', 'http://groovy.codehaus.org']
  params[0]=getTs(entry[0])

  Z.sql.execute 'insert into hs_log values (?, ?, ?,?,?)', params
}



File f=new File(args[0]);
//Pattern p=Pattern.compile("([0-9T:,\\-]+).*\\[(^[\\]]+)\\].*")
Pattern p=Pattern.compile("([0-9T:,\\-]+)\\s+(\\w+)\\s+\\[([^\\]]*)\\]\\s+([^:]+):\\s+(.*)")
def entry=null;
List<String> lines=f.readLines();
for(int lineNo=0;lineNo<lines.size();lineNo++) {
  s=lines.get(lineNo)
  println("...${lineNo}/${lines.size()}")
  Matcher m=p.matcher(s)
  //  println( m.matches())
  if(m.matches()) {
    if(entry != null)
      save(entry)
    entry=[];
    for(int i=1;i<=m.groupCount();i++) {
      entry.add(m.group(i))
      //      println( m.group(i))
    }
  }else {
    entry[4]+="\n"+s;
    //    println(s)
  }
  //  println( m.groupCount())
  //  break;
}

if(entry!=null)
  save(entry)

//import groovy.sql.Sql
//
//def dbUrl      = "jdbc:postgresql://localhost/test-db"
//def dbUser     = "test"
//def dbPassword = "test"
//def dbDriver   = "org.postgresql.Driver"
//
//def sql = Sql.newInstance(dbUrl, dbUser, dbPassword, dbDriver)

//File file1 = new File();
//  for (def i=0;i<=30;i++) // specify how many line need to read eg.. 30
//  {
//   log.info file1.readLines().get(i)
//
//  }