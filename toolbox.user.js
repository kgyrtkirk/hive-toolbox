// ==UserScript==
// @name         hu.rxd.hive.toolbox
// @namespace    http://tampermonkey.net/
// @version      0.3
// @description  adds some things...
// @author       kirk
// @match        https://issues.apache.org/jira/browse/**
// @match        https://builds.apache.org/job/PreCommit-HIVE-Build/*/testReport/
// @match        http://sust-j3.duckdns.org:8080/**/*hive*/*/testReport/**
// @match        http://sustaining-jenkins.eng.hortonworks.com:8080/**/*hive*/*/testReport/**
// @grant        none
// @require http://code.jquery.com/jquery-latest.js
// @require https://bowercdn.net/c/urijs-1.19.1/src/URI.min.js
// ==/UserScript==

(function() {
    'use strict';

    // credit:    https://stackoverflow.com/a/4673436/1525291
    if (!String.prototype.format) {
        String.prototype.format = function() {
            var args = arguments;
            return this.replace(/{(\d+)}/g, function(match, number) {
                return typeof args[number] != 'undefined'
                    ? args[number]
                : match
                ;
            });
        };
    }


    var style = $(`<style>
.toolbox_button {
color: blue;
xbackground-color: lightblue;
border: 1px solid lightblue;
    margin:-1px;
    margin-left:1px;
    margin-right: 5px;
width:1em;
    text-align: center;
    text-decoration: none !IMPORTANT;
    display: inline-block;
}
.toolbox_button:hover {
background-color: lightblue;
}
</style>
`);
    $('html > head').append(style);

    //$("a[title='Show details']").css( "border", "3px double red" );

    function createLink(label,link){
        var newLink=$("<a>", {
            title: label,
            href: link,
            class: "toolbox_button"
        }).append( label );
        return newLink;
    }

    function jiraSearch(jql){
        var args = {
            jql:jql
        };
        var uri=URI('https://issues.apache.org/jira/issues/').search(args);
        return uri;
    }

    function relatedTicketsSearch(testInfo) {
        var kwPart=testInfo.keywords.map(function(kw) {
            return ' ( summary ~ "{0}" or description ~ "{0}" or description ~ "{0}.q" )'.format(kw);
        }).join("\n and ");
       return jiraSearch(kwPart + "\nand project = hive order by updated desc");
    }

    function getTestOpts(testInfo) {
        // FIXME: possibly remove mavenPattern from testInfo
        // ultimate: https://api.github.com/search/code?q=filename:TestCliDriver.java+repo:apache/hive
        var testOpts='-Dtest={0}'.format(testInfo.mavenPattern);
        if(testInfo.testClassFull.toLowerCase().indexOf("spark") == -1) {
            testOpts+="\n-DskipSparkTests";
        }
        switch(testInfo.testClass){
            case "TestCliDriver":
            case "TestNegativeCliDriver":
            case "TestMiniLlapCliDriver":
            case "TestMiniLlapLocalCliDriver":
                testOpts+="\n-pl itests/qtest";
                break;
            default:
        }
        return testOpts;
    }

    function buildJobInvocationUri(jobName,testInfo) {
        var testOpts=getTestOpts(testInfo);
        var args={
            KEYWORD: 'R[{0}]'.format(testInfo.mavenPattern),
            M_TEST_OPTS: testOpts
        };
        var u=URI('http://sustaining-jenkins.eng.hortonworks.com:8080/view/hive/job/{0}/parambuild/'.format(jobName)).search(args);
        return u;
    }

    function createTestInfo(txt){
        var ret={};
        var tparts=txt.split(".");
        ret.keywords=[];
        ret.testMethod=tparts.pop();
        ret.testClassFull=tparts.join(".");
        ret.testClass=tparts.pop();
        ret.mavenPattern='{0}#{1}'.format(ret.testClass,ret.testMethod);
        ret.keywords.push(ret.testClass);
        var p = ret.testMethod.replace("]","").split(/\[/);
        if(p.size() == 2 ){
            ret.testParam=p.last();
            ret.keywords.push(ret.testParam);
        }
        return ret;
    }

    // $("tr:has( > td > a[title='Show details'])").css( "border", "3px double green" );
    function processFailureRow(row){
        // $(row).css( "border", "3px double brown" );
        var testLink=$(row).find("td:first-child a[href]");
        var testInfo=createTestInfo(testLink.text());

        var newLinks=[
            createLink("L",relatedTicketsSearch(testInfo)),
            createLink("R",buildJobInvocationUri('hive-check',testInfo)),
            createLink("B",buildJobInvocationUri('hive-bisect',testInfo)),
            ];
        newLinks.each(function (item) {
            item.insertBefore(testLink);
        });
//        testLink.css( "border", "3px double blue" );
    }

    $("tr:has( > td > a[title='Show details'])").each( function() {
        processFailureRow(this);
    });


    function collapseQAComments(){
        $(".activity-comment:has(a[rel=hiveqa]):not(:last)")
        .removeClass("extended")
        .addClass("collapsed");
    }
    collapseQAComments();


    function fixAttachmentSortOrder() {

        var p=$('ol:has(>li.attachment-content)');

        $('li.attachment-content').sort(function (a, b) {
            var contentA =parseInt($(a).attr('data-attachment-id'));
            var contentB =parseInt($(b).attr('data-attachment-id'));
            console.log(contentA);
            return (contentA-contentB);
        }).appendTo(p);
    }

    function getAttachments() {
        return $('li.attachment-content').map( function() {
            return {
                name:$(this).find("a").text(),
                time:$(this).find('time').attr('datetime'),
                attachmentId:parseInt($(this).attr('data-attachment-id')),
                url:$(this).find("a").attr('href'),
            }; } ).sort(function(a,b) { return + a.attachmentId - b.attachmentId; });
    }

    fixAttachmentSortOrder();

    function extractTicketId(str){
        return str.replace(/.*\//,'');
    }

    var ticketId = extractTicketId(window.location.pathname);

    function buildReExecJobInvocationUri(branch,qaInfo,patchUrl) {
        var jobName="hive-ptest-rerun";
        var args={
            KEYWORD: 'R[{2}@{0}@{1}]'.format(patchUrl.match(/[^\/]+$/),branch,ticketId),
            PTEST_JOB_URL: qaInfo.buildUrl,
            PATCH_URL: patchUrl,
        };
  //      alert(args.KEYWORD);
        var u=URI('http://sustaining-jenkins.eng.hortonworks.com:8080/view/hive/job/{0}/parambuild/'.format(jobName)).search(args);
        return u;
    }


    function decorateLastQA() {
        var c=$(".activity-comment:has(a[rel=hiveqa]):last");
        var qaInfo={
            patchUrl: c.find("a.external-link:contains('/attachment/')").text(),
            buildUrl: c.find("a.external-link:contains('/job/')").last().text().match(/.*\/[0-9]+/)+"/"
        };
        if(c.size() == 0 )
            return;
        var c2=c.find(".preformatted");
//        c2.css( "border", "3px double red" );
        createLink("A",buildReExecJobInvocationUri("apache/master",qaInfo,qaInfo.patchUrl)).insertAfter(c2);
        createLink("B",buildReExecJobInvocationUri("apache/master",qaInfo,"")).insertAfter(c2);
    }

    decorateLastQA();


})();
