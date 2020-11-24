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

function setAlt1Validity(valid) {
    if (valid) {
        $("#alternative1").removeClass("is-invalid").addClass("is-valid")
    } else {
        $("#alternative1").removeClass("is-valid").addClass("is-invalid")
    }
}

function setAlt2Validity(valid) {
    if (valid) {
        $("#alternative2").removeClass("is-invalid").addClass("is-valid")
    } else {
        $("#alternative2").removeClass("is-valid").addClass("is-invalid")
    }
}

// This is purely to debug with
let wasValid = false
function toggleAll() {
    setParticipantsValidity(wasValid)
    setDescriptionValidity(wasValid)
    setAlt1Validity(wasValid)
    setAlt2Validity(wasValid)
    wasValid = !wasValid
}

$("#createButton").click(toggleAll)