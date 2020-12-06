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

    clearReport()
    for (let c of choices){
        console.log("text");
        addChoiceLine(c["id"].split("-")[0] + "... " + c["description"].substring(0, 25), c["dateCreated"].split(" ")[0], c["dateCompleted"])
    }
}

function addChoiceLine(id, created, completed){
    let container = $("#report-container")[0]

    let item = document.createElement("li");
    let row = document.createElement("div");
    let stat = document.createElement("div");
    let info = document.createElement("div");
    let infoRow = document.createElement("div")
    let info1 = document.createElement("div");
    let info2 = document.createElement("div");

    item.className= "list-group-item";
    row.className = "row"
    info.className = "col-md-9"
    infoRow.className = "row"
    info1.className = "col-md-7"
    info2.className = "col-md-3"

    stat.classList = ["col-md-3", "text-center"]
    
    info1.innerText = id;
    info2.innerText = created
    
    if (completed == undefined){
        stat.classList.add("bg-warning")
        stat.innerText = "Incomplete"
    }
    else {
        stat.classList.add("bg-success")
        stat.innerText = completed.split(" ")[0]
    }
    infoRow.appendChild(info1)
    infoRow.appendChild(info2)
    info.appendChild(infoRow)
    row.appendChild(info)
    row.appendChild(stat)
    item.appendChild(row)
    container.appendChild(item)
}

function getAndLoadReport(){
    clearReport()
    addChoiceLine("Loading...", "Loading...", "Loading...")
    getAndLoadAsync().then();
}

function clearReport() {
    let container = $("#report-container")[0]
    let children = container.children
    for(let i = 0; i < children.length; i++){
        container.removeChild(children[i])
    }
}

$(document).ready(function() {
    item_template = $("#report-container")[0].children[0].innerHTML
    getAndLoadReport();
})

$(document).on("click", "#report-button", function (e){
    getAndLoadReport();
})
