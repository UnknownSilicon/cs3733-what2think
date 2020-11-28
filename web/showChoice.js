let GET_CHOICE_URL = "https://dz8pxyqdre.execute-api.us-east-1.amazonaws.com/beta/choice/"

let ALTERNATIVE_LIST = [$("#alt1"), $("#alt2"), $("#alt3"), $("#alt4"), $("#alt5")]

let ALTERNATIVE_DESC = [$('#alt1-desc'), $('#alt2-desc'), $('#alt3-desc'), $('#alt4-desc'), $('#alt5-desc')]

let ALTERNATIVE_UP = [$("#alt1-up-count"), $("#alt2-up-count"), $("#alt3-up-count"), $("#alt4-up-count"), $("#alt5-up-count")]
let ALTERNATIVE_DOWN = [$("#alt1-down-count"), $("#alt2-down-count"), $("#alt3-down-count"), $("#alt4-down-count"), $("#alt5-down-count")]

let FEEDBACK_LISTS = [$("#alt1-feedbacks"), $("#alt2-feedbacks"), $("#alt3-feedbacks"), $("#alt4-feedbacks"), $("#alt5-feedbacks")]

let CHOICE_TITLE = $("#choiceTitle")[0]

let PARTICIPANT_TAG = $("#participant-count")[0]

let thisChoice;

// function lovingly taken from stackoverflow
// https://stackoverflow.com/questions/979975/how-to-get-the-value-from-the-get-parameters
function parse_query_string(query) {
    let lets = query.split("&");
    let query_string = {};
    for (let i = 0; i < lets.length; i++) {
        let pair = lets[i].split("=");
        let key = decodeURIComponent(pair[0]);
        let value = decodeURIComponent(pair[1]);
        // If first entry with this name
        if (typeof query_string[key] === "undefined") {
            query_string[key] = decodeURIComponent(value);
            // If second entry with this name
        } else if (typeof query_string[key] === "string") {
            let arr = [query_string[key], decodeURIComponent(value)];
            query_string[key] = arr;
            // If third or later entry with this name
        } else {
            query_string[key].push(decodeURIComponent(value));
        }
    }
    return query_string;
}

function showAlternative(index) {
    ALTERNATIVE_LIST[index].removeClass("d-none")
}

function hideAlternative(index) {
    ALTERNATIVE_LIST[index].addClass("d-none")
}

async function getChoice(id) {
    const response = await fetch(GET_CHOICE_URL + id, {
        method: 'GET',
        mode: 'cors',
        cache: 'no-cache',
        credentials: 'omit',
    })

    return response.json() // Returns the Choice object
}

function createFeedback(feedback) {
    let quote = document.createElement("blockquote")
    let p = document.createElement("p")
    let footer = document.createElement("footer")

    quote.className = "blockquote p-2 border"
    p.className = "mb-0"
    footer.className = "blockquote-footer"

    p.innerText = feedback["content"]

    footer.innerText = feedback["user"]["name"] + " - " + feedback["timestamp"]

    quote.appendChild(p)
    quote.appendChild(footer)

    return quote
}

function loadChoice(choice) {
    console.log(choice)

    CHOICE_TITLE.innerText = "Choice " + choice["id"]

    $("#choice-desc")[0].innerText = choice["description"]

    let maxUsers = choice["maxUsers"]
    let usersCount = choice["users"].length

    PARTICIPANT_TAG.innerText = "Participants: " + usersCount + "/" + maxUsers

    let alternatives = choice["alternatives"]

    for (let i=0; i<alternatives.length; i++) {
        ALTERNATIVE_DESC[i][0].innerText = alternatives[i]["content"]

        let approvers_count = alternatives[i]["approvers"].length
        let disapprovers_count = alternatives[i]["disapprovers"].length

        ALTERNATIVE_UP[i][0].innerText = approvers_count
        ALTERNATIVE_DOWN[i][0].innerText = disapprovers_count

        let feedbacks = alternatives[i]["feedback"]

        for (let feedback of feedbacks) {
            let feedbackElement = createFeedback(feedback)

            FEEDBACK_LISTS[i][0].appendChild(feedbackElement)
        }

        showAlternative(i)
    }

    // TODO: Make completed marker somewhere
}

$(document).ready(function (){
    let queryData = parse_query_string(window.location.search.substring(1))
    let id = queryData["id"]
    if (id === undefined) {
        // Show some sort of error message or redirect to 404 page
        console.log("Choice is undefined!")
    } else {
        getChoice(id).then(
            data => {
                let statusCode = data["statusCode"]

                if (statusCode === 200) {
                    // Continue loading choice
                    thisChoice = data["choice"]

                    loadChoice(thisChoice)
                } else {
                    // Show error message or redirect to 404 page of sorts
                    console.log("Choice does not exist!")
                }
            }
        )
    }
})