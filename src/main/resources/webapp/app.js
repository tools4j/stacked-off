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
        '/indexes': function (params) {
            console.log("/indexes")
            fetchJson("/rest/indexes", indexStats => showIndexStats(indexStats))
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

function showIndexStats(indexStats) {
    const markup = `
        <h1>Indexes</h1>
        <table class="data-table">
            <tr><th>index</th><th># docs</th></tr>
            <tr><td>questionIndex</td><td>${indexStats.indexSizes.questionIndex}</td></tr>
            <tr><td>indexedSiteIndex</td><td>${indexStats.indexSizes.indexedSiteIndex}</td></tr>
            <tr><td>stagingPostIndex</td><td>${indexStats.indexSizes.stagingPostIndex}</td></tr>
            <tr><td>stagingCommentIndex</td><td>${indexStats.indexSizes.stagingCommentIndex}</td></tr>
            <tr><td>stagingUserIndex</td><td>${indexStats.indexSizes.stagingUserIndex}</td></tr>
        </table>`;
    $("#content")[0].innerHTML = markup
}

function showQuestion(question) {
    const markup = `
        <h1>${question.title}</h1>
        <div class="question">
            ${renderPost(question, question.indexedSite.seSite, null)}
            ${question.answers.length == 0 ? "" : `
                <h2>${question.answers.length} Answer${question.answers.length > 1 ? 's' : ''}</h2>`}
        </div>
        ${question.answers.length == 0 ? "" : `
            <div class="answers">
                ${question.answers.map(post => `
                <div class="answer"> 
                    ${renderPost(post, question.indexedSite.seSite, question.acceptedAnswerId)}
                </div>`)}     
            </div>`}`;
    $("#content")[0].innerHTML = markup
}

function renderPost(post, seSite, acceptedAnswerId){
    return `
    <table class="post">
        <tr>
            <td class="score-details">
            <span class="score">${post.score}</span>
            ${post.favoriteCount != null && post.favoriteCount > 0 ? `
                <div class="favorite-count">
                    <img class="star" display="block" width="18" src="static/star.png"/>
                    <div class="fav-count">${post.favoriteCount}</div>    
                </div>`: ""}
            ${acceptedAnswerId != null && post.id == acceptedAnswerId ? `
                <div class="favorite-count">
                    <img class="tick" display="block" width="18" src="static/tick.png"/>
                </div>`: ""}
            </div>
            </td>
            <td>
                <div class="post-body">
                    ${post.body}
                </div>            
                <div class="post-details">
                    <div class="user-details rounded-blue-box">
                        <div class="asked">${post.parentId == null ? 'asked': 'answered'} ${formatDate(post.creationDate)}</div>
                        <div class="display-name">${post. userDisplayName}</div>
                        <div class="reputation">${post.userReputation}</div>
                    </div> 
                    ${post.tags == null ? '': `                                               
                    <span class="tags">
                        ${post.tags
                            .split("><")
                            .map(tag => tag.replace("<", "").replace(">", ""))
                            .map(tag => `<span class="tag rounded-blue-box">${tag}</span>`)
                            .join('')}    
                    </span>`}
                    ${post.parentId != null ? '': `
                    <span>
                        <a class="online-link" href="${seSite.url}/questions/${post.id}">jump to online version</a>
                    </span>`}
                    <span class="last-activity">
                        edited ${formatDate(post.lastActivityDate)}
                    </span>
                </div>
                <table class="comments">
                    ${post.comments.map(comment =>
                    `<tr class="comment">
                        <td class="comment-score">
                            ${comment.score > 0 ? comment.score: ""}
                        </td>
                        <td class="comment-content">
                            <span class="comment-text">${comment.text}</span>
                            <span class="comment-user">&#8211;&nbsp;${comment.userDisplayName}</span>
                            <span class="comment-datetime">${formatDate(comment.creationDate)}</span>
                        </td>
                    </tr>`).join('\n')}
                </table>
            </td>
        </tr>       
    </table>`
}



function showResults(results){
    const markup = `
                ${results.map(question =>
        `<div class="result">
                        <h2 class="results-heading"><a class="result-link" data-navigo href="/questions/${question.uid}">
                            ${question.title}:&nbsp;${question.indexedSite.seSite.urlDomain}
                        </a></h2>
                    </h3>
                    <div class="result-body">${question.body}</div></div>`).join('')};
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
                            <th>Operation</th>
                        </tr>
                        ${indexedSites.map(indexedSite =>
                        `<tr>
                            <td>${indexedSite.indexedSiteId}</td>
                            <td>${indexedSite.seSite.tinyName}</td>
                            <td>${indexedSite.seSite.name}</td>
                            <td>${indexedSite.seSite.url}</td>
                            <td>${indexedSite.status.toLowerCase().replace("_", " ")}</td>
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

function formatDate(dateStr){
    return dateStr.replace('T', ' ').replace(/:\d\d\.\d\d\d/, '')
}

showStatusOnlyIfRunning();