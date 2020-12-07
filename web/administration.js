let GET_REPORT_URL = "https://dz8pxyqdre.execute-api.us-east-1.amazonaws.com/beta/aaaaaaaaadmin/choices"
let item_template = ""

function loadReport() {
    
}

async function getReport() {
    const response = await fetch(GET_REPORT_URL, {
        method: 'GET',
        mode: 'cors',
        cache: 'no-cache',
        credentials: 'omit',
    })

    return response.json() // Returns the Choice object
}

async function getAndLoadAsync() {
    return getReport().then(data => {
        let statusCode = data["statusCode"]
        if (statusCode == 200){
            loadReport(data["choices"])
        }
        else {
            console.log("request failed :(")
        }

    })
}

function loadReport(choices) {
    choices.sort(function (a, b) {
        return b["dateCreated"].localeCompare(a["dateCreated"])
    })
    clearReport()
    for (let c of choices){
        addChoiceLine(c["id"], c["description"], c["dateCreated"], c["dateCompleted"])
    }
}

function addChoiceLine(id, description, created, completed){
    let container = $("#report-container")[0]

    let item = document.createElement("li");
    let idlink = document.createElement("a");
    let iddiv = document.createElement("div");
    let row = document.createElement("div");
    let stat = document.createElement("div");
    let info = document.createElement("div");
    let infoRow = document.createElement("div");
    let info1 = document.createElement("div");
    let info2 = document.createElement("div");

    item.className= "list-group-item";
    row.className = "row"
    info.className = "col-md-9"
    infoRow.className = "row"
    info1.className = "col-md-7"
    info2.className = "col-md-3"
    idlink.className = "text-muted"

    stat.classList = ["col-md-3", "text-center"]
    
    idlink.innerText = id
    iddiv.style = "cursor: pointer;"
    idlink.href =  window.location.origin + "/showChoice.html?&id=" + id
    iddiv.appendChild(idlink);

    if(description.length > 30){
        description = description.substring(0, 27) + "..."
    }
    info1.innerText = description;
    info2.innerText = dhmFromDatetime(created)
    
    if (completed == undefined){
        stat.classList.add("text-warning")
        stat.innerText = "Incomplete"
    }
    else if (completed != ""){
        stat.classList.add("text-success")
        stat.innerText = completed.split(" ")[0] + " ✓"
    }
    infoRow.appendChild(info1)
    infoRow.appendChild(info2)
    info.appendChild(infoRow)
    row.appendChild(info)
    row.appendChild(stat)
    item.appendChild(iddiv)
    item.appendChild(row)
    container.appendChild(item)
}

function dhmFromDatetime(datetime) {
    if(datetime == ""){return ""}
    let time = datetime.split(" ")[0]
    let hms = datetime.split(" ")[1].split(":")
    return time +  " " + hms[0] + ":" + hms[1]
}

function getAndLoadReport(){
    clearReport()
    addChoiceLine("Loading...", "", "", "")
    getAndLoadAsync().then();
}

function clearReport() {
    let container = $("#report-container")[0]
    container.innerHTML = ""
}

$(document).ready(function() {
    item_template = $("#report-container")[0].children[0].innerHTML
    getAndLoadReport();
})

$(document).on("click", "#report-button", function (e){
    getAndLoadReport();
})
