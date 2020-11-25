CREATE_CHOICE_URL = "https://dz8pxyqdre.execute-api.us-east-1.amazonaws.com/beta/choice"

let ALTERNATIVE_TAGS = [$("#alternative1"), $("#alternative2"), $("#alternative3"), $("#alternative4"), $("#alternative5")]

function createChoiceObj() {
    let descriptionVal = $("#createDescription").val();
    if (descriptionVal.length <= 0 || descriptionVal.length > 350) {
        setDescriptionValidity(false)
    } else {
        setDescriptionValidity(true)
    }

    let participantsVal = $("#numParticipants").val()
    if (participantsVal < 1) {
        setParticipantsValidity(false)
    } else {
        setParticipantsValidity(true)
    }

    if (ALTERNATIVE_TAGS[0].val().length <= 0 || ALTERNATIVE_TAGS[0].val().length > 350) {
        setAltValidity(0, false)
    } else {
        setAltValidity(0, true)
    }

    if (ALTERNATIVE_TAGS[1].val().length <= 0 || ALTERNATIVE_TAGS[1].val().length > 350) {
        setAltValidity(1, false)
    } else {
        setAltValidity(1, true)
    }

    if (ALTERNATIVE_TAGS[2].val().length > 350) {
        setAltValidity(2, false)
    } else {
        setAltValidity(2, true)
    }

    if (ALTERNATIVE_TAGS[3].val().length > 350) {
        setAltValidity(3, false)
    } else {
        setAltValidity(3, true)
    }

    if (ALTERNATIVE_TAGS[4].val().length > 350) {
        setAltValidity(4, false)
    } else {
        setAltValidity(4, true)
    }

    let createChoice = {
        "description": descriptionVal,
        "maxUsers": participantsVal
    }
    let alternatives = []

    for (let a of ALTERNATIVE_TAGS) {
        if (a.val() !== "") {
            alternatives.push(a.val())
        }
    }
    createChoice["alternatives"] = alternatives

    return createChoice
}

async function createChoice(data) {
    const response = await fetch(CREATE_CHOICE_URL, {
        method: 'POST',
        mode: 'cors',
        cache: 'no-cache',
        credentials: 'omit',
        body: JSON.stringify(data)
    })

    return response.json() // Returns the Choice object
}

function setParticipantsValidity(valid) {
    if (valid) {
        $("#numParticipants").removeClass("is-invalid").addClass("is-valid")
    } else {
        $("#numParticipants").removeClass("is-valid").addClass("is-invalid")
    }
}

function setDescriptionValidity(valid) {
    if (valid) {
        $("#createDescription").removeClass("is-invalid").addClass("is-valid")
    } else {
        $("#createDescription").removeClass("is-valid").addClass("is-invalid")
    }
}

function setAltValidity(index, valid) {
    if (valid) {
        ALTERNATIVE_TAGS[index].removeClass("is-invalid").addClass("is-valid")
    } else {
        ALTERNATIVE_TAGS[index].removeClass("is-valid").addClass("is-invalid")
    }
}

// This is purely to debug with
let wasValid = false
function toggleAll() {
    setParticipantsValidity(wasValid)
    setDescriptionValidity(wasValid)
    setAltValidity(0, wasValid)
    setAltValidity(1, wasValid)
    setAltValidity(2, wasValid)
    setAltValidity(3, wasValid)
    setAltValidity(4, wasValid)
    wasValid = !wasValid
}

let CREATE_BUTTON = $("#createButton")

function buttonLoading() {
    CREATE_BUTTON.html("Loading...")
    CREATE_BUTTON.removeClass("btn-success").removeClass("btn-danger").addClass("btn-primary")
}

function buttonNormal() {
    CREATE_BUTTON.html("Create!")
    CREATE_BUTTON.removeClass("btn-success").removeClass("btn-danger").addClass("btn-primary")
}

function buttonSuccess() {
    CREATE_BUTTON.html("Success!")
    CREATE_BUTTON.removeClass("btn-primary").removeClass("btn-danger").addClass("btn-success")
}

function buttonError() {
    CREATE_BUTTON.html("Error!")
    CREATE_BUTTON.removeClass("btn-primary").removeClass("btn-success").addClass("btn-danger")
}

$(document).on('click', '#createButton', function(e) {
    buttonLoading();
    createChoice(createChoiceObj())
        .then(data => {
            let statusCode = data["statusCode"]

            if (statusCode === 200) {
                buttonSuccess()

                let id = data["choice"]["id"]
                // window.location.href = window.location.origin + "/choice?&id=" + id
                console.log(id)
            } else {
                buttonError()

                setTimeout(function () {
                    buttonNormal()
                }, 3000)
            }
        })
})