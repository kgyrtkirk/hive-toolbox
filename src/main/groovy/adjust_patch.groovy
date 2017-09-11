
import groovyx.net.http.RESTClient
import static groovyx.net.http.ContentType.*


if( args.length < 1 ){
	throw new RuntimeException("need args: [JIRAKEY-7]")
}

def twitter = new RESTClient( 'http://issues.apache.org/jira/rest/api/2/' )

def jiraKey=this.args[0]

try{
	twitter.get( path: 'search', query: ['jql':"key=${jiraKey}"] ) { resp, reader ->
		def issue=reader.issues[0]
		def key=issue.key
		def summary= issue.fields.summary
		def assignee=reader.issues[0].fields.assignee
		
		def author="${assignee.displayName}"
		def authorIdent="${assignee.displayName} <${assignee.emailAddress.replaceAll(' at ','@').replaceAll(' dot ','.')}>"

		println reader.issues[0].keySet()
		println reader.issues[0].fields.keySet()
		println reader.issues[0].fields.assignee
		msg=("${key}: ${summary} (${author} via Zoltan Haindrich)")
		println("${msg}")
		println(authorIdent)

		println ""
		println "git commit --author '${authorIdent}' -m '${msg}' --signoff"
	}
}catch( ex ){
	//println(ex.response)
	throw ex;
}
