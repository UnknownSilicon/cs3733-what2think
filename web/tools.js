let altHTML = saveAltHTML();
let queryData = parse_query_string(window.location.search.substring(1));
let CHOICE_ID = queryData["id"];
loadBasedOnID(CHOICE_ID);

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

function loadBasedOnID(id) {
	let choice_display = document.getElementById("choice-display");
	let choice_create = document.getElementById("choice-create");
	let body = document.getElementById("body");
	if (typeof id === "undefined") {
		body.removeChild(choice_display);
		setupCreateInput()
	} else {
		body.removeChild(choice_create);
		setupChoiceInput()
		// Actually go load the choice. Call the API and do the things
	}
}

function setupCreateInput() {
	document.getElementById("create-choice-button").onclick = onCreateClick;
}

function setupChoiceInput() {
	document.getElementById("signinButton").onclick = onSignInClick;
}


// CHOICE DISPLAY CODE //////////

function saveAltHTML() {
	return document.getElementById("alternative-container").innerHTML;
}

function clearAlts() {
	let container = document.getElementById("alternative-container");
	while(container.lastElementChild){
		container.removeChild(container.lastElementChild);
	}
}

function appendAlt(altJSON) {
	let container = document.getElementById("alternative-container");
	let alt = document.createElement("div");
	container.appendChild(alt);
	
	alt.innerHTML = altHTML;
	clearFeedback(alt);

	alt.getElementsByClassName("alt-num")[0].innerHTML = "Alternative " + altJSON["id"] + ":";
	alt.getElementsByClassName("alt-content")[0].innerHTML = altJSON["content"];
	//TODO: Display votes somewhere?
	for(feedback of altJSON["feedback"]) {
		addFeedback(alt, feedback);
	}


}

function clearFeedback(alternative){
	let feedbacks = alternative.getElementsByClassName("feedbacks-container")[0];
	console.log(feedbacks);
	if (feedbacks === undefined) return;
	while(feedbacks.lastElementChild){
		feedbacks.removeChild(feedbacks.lastElementChild);
	}
}

function addFeedback(alternative, feedbackJSON) {
	let feedback = document.createElement("div");
	let content = document.createElement("div");
	let meta = document.createElement("div");
	
	feedback.className = "feedback";
	
	content.className = "feedback-box bordered";
	
	meta.className = "feedback-info bordered";
	feedback.appendChild(content);
	feedback.appendChild(meta);
	content.innerHTML = feedbackJSON["content"];
	meta.innerHTML = feedbackJSON["user"] + " - " + feedbackJSON["timestamp"];
	
	let feedbacks = alternative.getElementsByClassName("feedbacks-container")[0];
	feedbacks.appendChild(feedback);
}

// CHOICE INPUT CODE //////////

CREATE_CHOICE_URL = "https://dz8pxyqdre.execute-api.us-east-1.amazonaws.com/beta/choice"

function onCreateClick(e){
  	let js = createChoiceJSON();
	let xhr = new XMLHttpRequest();
	xhr.open("POST", CREATE_CHOICE_URL, true);

	// send the collected data as JSON
	xhr.send(js);

	// This will process results and update HTML as appropriate. 
	xhr.onloadend = function () {
		console.log(xhr);
		console.log(xhr.request);
		if (xhr.readyState === XMLHttpRequest.DONE) {
			if (xhr.status === 200) {
				console.log ("XHR:" + xhr.responseText);
				let xhrJson = JSON.parse(xhr.responseText)
				let id = xhrJson["choice"]["id"]
				window.location.href = window.location.href + "&id=" + id
			} else if (xhr.status === 400) {
				alert ("unable to process request");
			}
		} else {
			console.log("wut");
			//processResponse(arg1, arg2, "N/A")
		}
	};
}

function createChoiceJSON(){
	let data = {};
	data["description"] = document.getElementById("description-input").value;
	data["maxUsers"] = parseInt(document.getElementById("participant-count-input").value, 10);
	data["alternatives"] = [];
	let inputs = document.getElementById("alternative-input-container").getElementsByClassName("alt-input");
	for (input of inputs) {
		if(input.value !== ""){
			data["alternatives"].push(input.value);
		}
	}
	return JSON.stringify(data);
}

// LOGIN CODE //////////

// Sorry, this is gross
REGISTER_URL_START = "https://dz8pxyqdre.execute-api.us-east-1.amazonaws.com/beta/choice/"
REGISTER_URL_END = "/registerUser"

function onSignInClick(e) {
	let username = document.getElementById("signInName").value
	let password = document.getElementById("signInPass").value

	let fullUrl = REGISTER_URL_START + CHOICE_ID + REGISTER_URL_END

	let data = {};
	data["user"] = {}
	data["user"]["name"] = username
	data["user"]["password"] = password

	let xhr = new XMLHttpRequest()
	xhr.open("POST", fullUrl, true)
	xhr.setRequestHeader("Content-Type", "application/json")
	xhr.send(JSON.stringify(data))

	xhr.onloadend = function () {
		console.log(xhr);
		console.log(xhr.request);
		if (xhr.readyState === XMLHttpRequest.DONE) {
			console.log("Response: " + xhr.responseText)
			let responseJson = JSON.parse(xhr.responseText)

			// This _should_ just look at the response code, but API gateway is dumb
			if (responseJson["statusCode"] === 200) {
				console.log("Logged in!")
				// Do things!
				document.getElementById("signedInMsg").innerText = "Signed in!"
			} else {
				let error = responseJson["error"]
				alert("Unable to process request: " + error)
			}
		} else {
			console.log("Something broke! You shouldn't be here!")
		}
	}
}
