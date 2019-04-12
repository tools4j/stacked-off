import Navigo from './navigo.js'

const root = document.location.href.replace(/((?:\w+:\/\/)?[^\/]+).*/, "$1")
console.log("root:" + root)
const router = new Navigo(root);

router
    .on({
        '/sites': function () {
            console.log("Resolved to /sites")
            fetchJson("/rest/sites", sites => showSites(sites));
        },
        '/search': function (params, queryString) {
            console.log("Resolved to /search")
            var queryParams = convertQueryStringToJson(queryString)
            fetchJson("/rest/search?searchText=" + queryParams.searchText, results => showResults(results));
        },
        '/questions/:questionUid': function (params) {
            console.log("Resolved to /questions/:questionUid")
            fetchJson("/rest/questions/" + params.questionUid, question => showQuestion(question));
        },
        '/load/chooseSitesXmlFile': function (params) {
            console.log("/load/chooseSitesXmlFile")
            loadNewSites_chooseSitesXmlFile()
        },
        '/load/selectSitesToLoad': function (params, queryString) {
            console.log("/load/selectSitesToLoad")
            var queryParams = convertQueryStringToJson(queryString)
            fetchJson("/rest/sedir?path=" + queryParams.path, seDirSites => loadNewSites_selectSitesToLoad(queryParams.path, seDirSites));
        },
        '/load/run': function (params, queryString) {
            console.log("/load/run")
            var queryParams = convertQueryStringToJson(queryString)
            fetchJson("/rest/loadSites?path=" + queryParams.path + "&seDirSiteIds=" + queryParams.seDirSiteIds, status => showStatusWhileRunning());
        },
        '/status': function (params) {
            console.log("/status")
            showStatusWhileRunning()
        },
        '*': function () {
            console.log("Resolved to wildcard")
            fetchJson("/rest/sites", sites => showSites(sites));
        }
    })
    .resolve();

router.purgeSite = purgeSite
export default router;

function sleep (time) {
    return new Promise((resolve) => setTimeout(resolve, time));
}

function convertQueryStringToJson(queryString) {
    var pairs = queryString.split('&');

    var result = {};
    pairs.forEach(function(pair) {
        pair = pair.split('=');
        result[pair[0]] = decodeURIComponent(pair[1] || '');
    });
    return JSON.parse(JSON.stringify(result));
}

function loadNewSites_chooseSitesXmlFile(){
    const markup = `<h1>Load new site(s)</h1>
                <h2>Step 1. Enter the path of the a downloaded stack dump directory</h2>
                <span>(This cannot be a file chooser due to browser security restrictions.)</span>
                <div class="div-load-sites">
                    <input id="sites-xml-chooser"
                            type="text"
                            value="C:/Users/ben/Downloads/stackexchange"
                            onchange="$('#sites-xml-chosen-next-button')[0].disabled=false"/>
                </div>
                <input id="sites-xml-chosen-next-button"
                    type="button"
                    value="Next"
                    onclick="router.navigate('/load/selectSitesToLoad?path=' + $('#sites-xml-chooser')[0].value.replace(/\\\\/g, '/'))">
                `;
    $("#content")[0].innerHTML = markup
}

function loadNewSites_selectSitesToLoad(seDir, seDirSites){
    // language=HTML
    const nextButton = `<input
                id="sites-selected-to-load-button"
                type="button"
                value="Next"
                disabled="true"
                onclick="router.navigate('/load/run?path=${seDir}&seDirSiteIds=' + $('.site-selection-checkbox:checkbox:checked').map(function(){return this.value}).get().join(','))"/>`;

    const markup = `<h1>Load new site(s)</h1>
            <h2>Step 2. Select the sites that you wish to load</h2>
            ${nextButton}
            <table class="data-table">
                <tr>
                <th>Id</th>
                <th>Site Url</th>
                <th>files</th>
                <th>load</th>
                </tr>
            ${seDirSites.map(seDirSite =>
               `<tr>
                    <td>${seDirSite.site.seSiteId}</td>
                    <td>${seDirSite.site.url}</td>
                    <td>${seDirSite.zipFiles.length}</td>
                    <td><input
                            type="checkbox"
                            class="site-selection-checkbox"
                            value="${seDirSite.site.seSiteId}"
                            onchange="$('#sites-selected-to-load-button')[0].disabled=false">
                    </td>
                </tr>`).join('')}
            </table>
            ${nextButton}
            `

    $("#content")[0].innerHTML = markup
}

function showStatusOnlyIfRunning(){
    fetchJson("/rest/status", status => {
        if(!status.running) return;
        showStatus(status);
        showStatusWhileRunning()
    });
}

function showStatusWhileRunning(){
    router.pause();
    router.navigate('/status');
    router.resume();
    fetchJson("/rest/status", status => {
        showStatus(status);
        if(!status.running) return;
        sleep(1000).then(() => {
            showStatusWhileRunning()
        });
    });
}

function loadStatus(){
    fetchJson("/rest/status", status => showStatus(status));
}

function showStatus(status){
    var statusStr = ""
    if(status.currentOperationProgress != "Complete") {
        statusStr += "<h1>Operation currently in progress...</h1>";
    } else {
        statusStr += "<h1>Operation complete</h1>";
    }
    statusStr += "<pre>=============================================================================\n";
    statusStr += status.operationHistory.join("\n");
    statusStr += "\n";
    if(status.currentOperationProgress != "") {
        statusStr += "-----------------------------------------------------------------------------\n";
        statusStr += status.currentOperationProgress + "\n";
    }
    statusStr += "=============================================================================</pre>"
    $("#content")[0].innerHTML = statusStr
    return status.running
}

function showQuestion(question) {
    const markup = `
                <h1>${question.post.rawPost.title}</h1>
                <div>${question.post.rawPost.body}</div>
                ${question.post.comments.map(comment =>
        `<div class="comment">
                        <div class="comment-content">
                            ${comment.rawComment.text}
                        </div>
                        <div>
                            ${comment.user.displayName}
                        </div>
                    </div>`).join('\n')}`
    $("#content")[0].innerHTML = markup
}

function showResults(results){
    const markup = `
                ${results.map(question =>
        `<div class="result">
                        <h2 class="results-heading"><a class="result-link" data-navigo href="/questions/${question.post.rawPost.indexedSiteId}.${question.post.rawPost.id}">
                            ${question.post.rawPost.title}:&nbsp;${question.indexedSite.seSite.urlDomain}
                        </a></h2>
                    </h3>
                    <div class="result-body">${question.post.rawPost.body}</div></div>`).join('')};
                    <script javascript="router.updatePageLinks()"/>`
    $("#content")[0].innerHTML = markup
}


function purgeSite(indexedSiteId){
    const confirmed = confirm("Do you wish to purge this site from your index?")
    if(confirmed){
        fetchJson("/rest/purgeSite/" + indexedSiteId, sites => showSites(sites));
    }
}


function showSites(indexedSites){
    if(indexedSites.length == 0){
        const markup = `
                <span>You have no sites loaded.  Click here to load sites from a stackdump download</span>
                <input type="button" onclick="router.navigate('/load/chooseSitesXmlFile')" value="Load new site(s)"/>
                `
        $("#content")[0].innerHTML = markup
    } else {
        const markup = `
                    <table class="data-table content-start-spacer">
                        <tr>
                            <th>Id</th>
                            <th>TinyName</th>
                            <th>Name</th>
                            <th>Url</th>
                            <th>Loaded</th>
                            <th>Errors</th>
                            <th>Operation</th>
                        </tr>
                        ${indexedSites.map(indexedSite =>
                        `<tr>
                            <td>${indexedSite.indexedSiteId}</td>
                            <td>${indexedSite.seSite.tinyName}</td>
                            <td>${indexedSite.seSite.name}</td>
                            <td>${indexedSite.seSite.url}</td>
                            <td>${indexedSite.success ? `Success`: `Error`}</td>
                            <td><input type="button" value="Delete" onclick="router.purgeSite('${indexedSite.indexedSiteId}')"/></td>
                        </tr>`).join('')}
                    </table>`;
        $("#content")[0].innerHTML = markup
    }
}

function fetchJson(address, callback) {
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            var obj = JSON.parse(this.responseText);
            $("#errors")[0].innerHTML = ""
            callback(obj)
        } else if(this.status == 500 || this.status == 404){
            $("#errors")[0].innerHTML = this.responseText
        }
    };
    xmlhttp.open("GET", address , true);
    xmlhttp.send();
}

showStatusOnlyIfRunning();