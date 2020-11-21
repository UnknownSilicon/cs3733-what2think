var altHTML = saveAltHTML();

function saveAltHTML() {
	return document.getElementById("alternative-container").innerHTML;
}

function clearAlts() {
	var container = document.getElementById("alternative-container");
	while(container.lastElementChild){
		container.removeChild(container.lastElementChild);
	}
}

function appendAlt(altJSON) {
	var container = document.getElementById("alternative-container");
	var alt = document.createElement("div");
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
	var feedbacks = alternative.getElementsByClassName("feedbacks-container")[0];
	console.log(feedbacks);
	if (feedbacks == undefined) return;
	while(feedbacks.lastElementChild){
		feedbacks.removeChild(feedbacks.lastElementChild);
	}
}

function addFeedback(alternative, feedbackJSON) {
	var feedback = document.createElement("div");
	var content = document.createElement("div");
	var meta = document.createElement("div");
	
	feedback.className = "feedback";
	
	content.className = "feedback-box bordered";
	
	meta.className = "feedback-info bordered";
	feedback.appendChild(content);
	feedback.appendChild(meta);
	content.innerHTML = feedbackJSON["content"];
	meta.innerHTML = feedbackJSON["user"] + " - " + feedbackJSON["timestamp"];
	
	var feedbacks = alternative.getElementsByClassName("feedbacks-container")[0];
	feedbacks.appendChild(feedback);
}


