let GET_CHOICE_URL = "https://dz8pxyqdre.execute-api.us-east-1.amazonaws.com/beta/choice/"
let CHOICE_ACTION_URL_START = "https://dz8pxyqdre.execute-api.us-east-1.amazonaws.com/beta/choice/"
let LOGIN_URL_END = "/registerUser"
let APPROVE_URL_END = "/approve"
let REMOVE_APPROVAL_URL_END = "/removeApproval"
let DISAPPROVE_URL_END = "/disapprove"
let REMOVE_DISAPPROVAL_URL_END = "/removeDisapproval"
let ADD_FEEDBACK_URL_END = "/addFeedback"
let COMPLETE_URL_END = "/complete"

let ALTERNATIVE_LIST = [$("#alt1"), $("#alt2"), $("#alt3"), $("#alt4"), $("#alt5")]

let ALTERNATIVE_CONTAINERS = [$("#alt1-cont"), $("#alt2-cont"), $("#alt3-cont"), $("#alt4-cont"), $("#alt5-cont")]

let ALTERNATIVE_DESC = [$('#alt1-desc'), $('#alt2-desc'), $('#alt3-desc'), $('#alt4-desc'), $('#alt5-desc')]

let ALTERNATIVE_UP_COUNT = [$("#alt1-up-count"), $("#alt2-up-count"), $("#alt3-up-count"), $("#alt4-up-count"), $("#alt5-up-count")]
let ALTERNATIVE_DOWN_COUNT = [$("#alt1-down-count"), $("#alt2-down-count"), $("#alt3-down-count"), $("#alt4-down-count"), $("#alt5-down-count")]

let ALTERNATIVE_UP_SELECTORS = ["#alt1-up", "#alt2-up", "#alt3-up", "#alt4-up", "#alt5-up"]
let ALTERNATIVE_DOWN_SELECTORS = ["#alt1-down", "#alt2-down", "#alt3-down", "#alt4-down", "#alt5-down"]
let ALTERNATIVE_COMPLETE_SELECTORS = ["#alt1-complete", "#alt2-complete", "#alt3-complete", "#alt4-complete", "#alt5-complete"]

let FEEDBACK_LISTS = [$("#alt1-feedbacks"), $("#alt2-feedbacks"), $("#alt3-feedbacks"), $("#alt4-feedbacks"), $("#alt5-feedbacks")]

let FEEDBACK_INPUTS = [$("#alt1-text"), $("#alt2-text"), $("#alt3-text"), $("#alt4-text"), $("#alt5-text")]

let FEEDBACK_POST_SELECTORS = ["#alt1-post", "#alt2-post", "#alt3-post", "#alt4-post", "#alt5-post"]

let CHOICE_TITLE = $("#choiceTitle")[0]

let PARTICIPANT_TAG = $("#participant-count")[0]

let LOGIN_BUTTON = $("#login-button")

let thisChoice;
let thisUser;

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
        if (alternatives[i] === null) continue
        ALTERNATIVE_DESC[i][0].innerText = alternatives[i]["content"]

        let approvers_count = alternatives[i]["approvers"].length
        let disapprovers_count = alternatives[i]["disapprovers"].length

        let approversText = "Voters: "
        let disapproversText = "Voters: "

        for (let a of alternatives[i]["approvers"]) {
            approversText += a["name"] + ", "
        }
        approversText = approversText.substring(0, approversText.length-2)
        $(ALTERNATIVE_UP_SELECTORS[i]).attr("data-content", approversText)

        for (let d of alternatives[i]["disapprovers"]) {
            disapproversText += d["name"] + ", "
        }
        disapproversText = disapproversText.substring(0, disapproversText.length-2)
        $(ALTERNATIVE_DOWN_SELECTORS[i]).attr("data-content", disapproversText)

        ALTERNATIVE_UP_COUNT[i][0].innerText = approvers_count
        ALTERNATIVE_DOWN_COUNT[i][0].innerText = disapprovers_count

        let feedbacks = alternatives[i]["feedback"]

        FEEDBACK_LISTS[i][0].innerHTML = ""
        for (let feedback of feedbacks) {
            let feedbackElement = createFeedback(feedback)

            FEEDBACK_LISTS[i][0].appendChild(feedbackElement)
        }

        showAlternative(i)
    }

    if (choice["completed"]) {
        // Find the completed alternative
        let chosenAlt = choice["chosenAlternative"]
        let altId = chosenAlt["id"]

        for (let i=0; i<5; i++) {
            if (choice["alternatives"][i]["id"] === altId) {
                markCompleted(i)
                break
            }
        }
    }
}

async function getAndLoadAsync(id) {
    return getChoice(id).then(
        data => {
            let statusCode = data["statusCode"]

            if (statusCode === 200) {
                // Continue loading choice
                thisChoice = data["choice"]
                LOGIN_BUTTON.removeAttr("disabled")

                loadChoice(thisChoice)
            } else {
                // Show error message
                console.log("Choice does not exist!")
                showInvalidChoice()
            }
            console.log("Done loading choice")
        }
    )
}

function getAndLoadChoice(id) {
   getAndLoadAsync(id).then()
}

function validateLogin() {
    let name = $("#login-name")
    let pass = $("#login-password")

    let success = true

    if (name.val().length <= 0 || name.val().length > 45) {
        name.removeClass("is-valid").addClass("is-invalid")
        success = false
    } else {
        name.removeClass("is-invalid").addClass("is-valid")
    }

    if (pass.val().length > 45) {
        pass.removeClass("is-valid").addClass("is-invalid")
        $("#invalid-pw-main").addClass("invalid-feedback").removeClass("d-none")
        $("#invalid-pw-second").addClass("d-none").removeClass("invalid-feedback")
        success = false
    } else {
        pass.removeClass("is-invalid").addClass("is-valid")
    }

    return success
}


function createUser() {
    let name = $("#login-name").val()
    let pass = $("#login-password").val()

    return {
        user: {
            "name": name,
            "password": pass
        }
    }
}

async function login(url, data) {
    const response = await fetch(url, {
        method: 'POST',
        mode: 'cors',
        cache: 'no-cache',
        credentials: 'omit',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })

    return response.json()
}

function showInvalidChoice() {
    hideLogin()
    hideDescription()
    CHOICE_TITLE.innerText = "Choice Invalid"
}

function hideLogin() {
    $("#login-card").addClass("d-none")
}

function hideDescription() {
    $("#description-card").addClass("d-none")
}

function loadUser() {
    console.log("Loading user")
    $("#login-card").addClass("d-none")
    $("#loggedin-card").removeClass("d-none")
    $("#logged-in-text").html("Logged In As <div class=\"text-success\">" + thisUser["name"] + "</div>")


    for (let i=0; i<5; i++) {
        if (thisChoice["alternatives"][i] == null) continue

        let approvers = thisChoice["alternatives"][i]["approvers"]
        let disapprovers = thisChoice["alternatives"][i]["disapprovers"]

        let isApprover = false
        for (let a of approvers) {
            if (a["name"] === thisUser["name"]) {
                // User approves this alternative
                $(ALTERNATIVE_UP_SELECTORS[i]).removeClass("btn-secondary").addClass("btn-success")
                isApprover = true
            }
        }

        if (!isApprover) {
            $(ALTERNATIVE_UP_SELECTORS[i]).removeClass("btn-success").addClass("btn-secondary")
        }

        let isDisapprover = false
        for (let d of disapprovers) {
            if (d["name"] === thisUser["name"]) {
                $(ALTERNATIVE_DOWN_SELECTORS[i]).removeClass("btn-secondary").addClass("btn-danger")
                isDisapprover = true
            }

        }

        if (!isDisapprover) $(ALTERNATIVE_DOWN_SELECTORS[i]).removeClass("btn-danger").addClass("btn-secondary")
    }

    if (!thisChoice["completed"]) {
        $(":button").removeAttr("disabled")
    }
}

function userApproves(altIndex) {
    let alt = thisChoice["alternatives"][altIndex]

    if (alt != null) {
        for (let a of alt["approvers"]) {
            if (a["name"] === thisUser["name"]) {
                return true
            }
        }
    }
    return false
}

function userDispproves(altIndex) {
    let alt = thisChoice["alternatives"][altIndex]

    if (alt != null) {
        for (let a of alt["disapprovers"]) {
            if (a["name"] === thisUser["name"]) {
                return true
            }
        }
    }
    return false
}

async function voteAction(url, altIndex) {
    let alt = thisChoice["alternatives"][altIndex]

    let data = {
        "altAction": {
            "user": thisUser,
            "alternative": alt
        }
    }

    console.log(data)

    if (alt != null) {
        const response = await fetch(CHOICE_ACTION_URL_START + thisChoice["id"] + url, {
            method: 'POST',
            mode: 'cors',
            cache: 'no-cache',
            credentials: 'omit',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        })

        return response.json()
    }
    return null
}

async function postFunction(choiceId, altId, content, user) {
    let data = {
        "alternativeId": altId,
        "user": user,
        "content": content
    }

    const response = await fetch(CHOICE_ACTION_URL_START + choiceId + ADD_FEEDBACK_URL_END, {
        method: 'POST',
        mode: 'cors',
        cache: 'no-cache',
        credentials: 'omit',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })

    return response.json()
}

function markCompleted(altNum) {
    for (let i=0; i<5; i++) {
        if (i !== altNum) {
            ALTERNATIVE_CONTAINERS[i].addClass("transparent-card")
        }
    }

    $(ALTERNATIVE_COMPLETE_SELECTORS[altNum]).removeClass("btn-primary").addClass("btn-success")

    $(":button").attr("disabled", "disabled")

    $("#login-button").removeAttr("disabled")
    $("#logout-button").removeAttr("disabled")

    $(".feedback-input").attr("disabled", "disabled")
}

async function completeChoice(choiceId, alternative) {
    let data = {
        "alternative": alternative
    }

    const response = await fetch(CHOICE_ACTION_URL_START + choiceId + COMPLETE_URL_END, {
        method: 'POST',
        mode: 'cors',
        cache: 'no-cache',
        credentials: 'omit',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })

    return response.json()
}

$(document).ready(function (){
    let queryData = parse_query_string(window.location.search.substring(1))
    let id = queryData["id"]
    if (id === undefined) {
        // Show some sort of error message
        console.log("Choice is undefined!")
        showInvalidChoice()
    } else {
        getAndLoadChoice(id)
    }

    $('[data-toggle="popover"]').popover()
})

$(document).on("click", "#login-button", function (e) {
    // Validate data
    if (validateLogin()) {
        LOGIN_BUTTON.html("Loading...")

        let registerRequest = createUser()

        login(CHOICE_ACTION_URL_START + thisChoice["id"] + LOGIN_URL_END, registerRequest).then(
            data => {
                if (data["statusCode"] === 200) {
                    // Logged in! Enable buttons and change the login card to the logged in card
                    thisUser = registerRequest["user"]

                    if (!thisChoice["completed"]) {
                        $(".feedback-input").removeAttr("disabled")
                    }

                    getAndLoadChoice(thisChoice["id"])

                    loadUser()
                } else {
                    // Fail! Show error
                    LOGIN_BUTTON.html("Error!")
                    LOGIN_BUTTON.removeClass("btn-primary").addClass("btn-danger")
                    $("#invalid-pw-second").addClass("invalid-feedback").removeClass("d-none")
                    $("#invalid-pw-main").addClass("d-none").removeClass("invalid-feedback")
                    $("#login-password").removeClass("is-valid").addClass("is-invalid")
                    setTimeout(function () {
                        LOGIN_BUTTON.html("Log In!")
                        LOGIN_BUTTON.removeClass("btn-danger").addClass("btn-primary")
                    }, 3000)
                }
            }
        )
    }
})

$(document).on("click", "#logout-button", function (e) {
    thisUser = undefined

    LOGIN_BUTTON.html("Log In!")
    $(":button").attr("disabled", "disabled")

    $("#login-button").removeAttr("disabled")

    $(".feedback-input").attr("disabled", "disabled")

    $("#login-card").removeClass("d-none")
    $("#loggedin-card").addClass("d-none")

})

for (let i=0; i<5; i++) {
    $(document).on("click", ALTERNATIVE_UP_SELECTORS[i], function (e) {
        // Approve/Remove approval
        $(ALTERNATIVE_UP_SELECTORS[i]).html("<i class=\"fas fa-spinner fa-spin\"></i>")
        if (userApproves(i)) {
            // Remove approval
            voteAction(REMOVE_APPROVAL_URL_END, i).then(
                data => {
                    getAndLoadAsync(thisChoice["id"]).then(data => {
                        loadUser()
                        $(ALTERNATIVE_UP_SELECTORS[i]).html("<i class=\"fas fa-thumbs-up\"></i>")
                    })
                }
            )
        } else {
            // Add approval
            voteAction(APPROVE_URL_END, i).then(
                data => {
                    getAndLoadAsync(thisChoice["id"]).then(data => {
                        loadUser()
                        $(ALTERNATIVE_UP_SELECTORS[i]).html("<i class=\"fas fa-thumbs-up\"></i>")
                    })

                }
            )
        }
    })
    $(document).on("click", ALTERNATIVE_DOWN_SELECTORS[i], function (e) {
        // Disapprove/Remove disapproval
        $(ALTERNATIVE_DOWN_SELECTORS[i]).html("<i class=\"fas fa-spinner fa-spin\"></i>")
        if (userDispproves(i)) {
            // Remove disapproval
            voteAction(REMOVE_DISAPPROVAL_URL_END, i).then(
                data => {
                    getAndLoadAsync(thisChoice["id"]).then(data => {
                        loadUser()
                        $(ALTERNATIVE_DOWN_SELECTORS[i]).html("<i class=\"fas fa-thumbs-down\"></i>")
                    })
                }
            )
        } else {
            // Add approval
            voteAction(DISAPPROVE_URL_END, i).then(
                data => {
                    getAndLoadAsync(thisChoice["id"]).then(data => {
                        loadUser()
                        $(ALTERNATIVE_DOWN_SELECTORS[i]).html("<i class=\"fas fa-thumbs-down\"></i>")
                    })
                }
            )
        }
        getAndLoadChoice(thisChoice["id"])
        loadUser()
    })

    $(document).on("click", FEEDBACK_POST_SELECTORS[i], function (e) {
        // Post feedback to alternative
        $(FEEDBACK_POST_SELECTORS[i]).html("Loading...")

        let content = FEEDBACK_INPUTS[i].val()

        if (content.length <= 0 || content.length > 350) {
            // Validation failed
            FEEDBACK_INPUTS[i].addClass("is-invalid")
            $(FEEDBACK_POST_SELECTORS[i]).html("Post")
        } else {
            FEEDBACK_INPUTS[i].removeClass("is-invalid")
            postFunction(thisChoice["id"], thisChoice["alternatives"][i]["id"], content, thisUser).then(
                data => {
                    $(FEEDBACK_POST_SELECTORS[i]).html("Post")
                    getAndLoadChoice(thisChoice["id"])
                }
            )
        }
    })

    $(document).on("click", ALTERNATIVE_COMPLETE_SELECTORS[i], function (e) {
        $(ALTERNATIVE_COMPLETE_SELECTORS[i]).html("<i class=\"fas fa-spinner fa-spin\"></i>")

        completeChoice(thisChoice["id"], thisChoice["alternatives"][i]).then(
            data => {
                getAndLoadAsync(thisChoice["id"]).then(
                    data => {
                        $(ALTERNATIVE_COMPLETE_SELECTORS[i]).html("<i class=\"fas fa-check\"></i>")
                    }
                )
            }
        )
    })
}