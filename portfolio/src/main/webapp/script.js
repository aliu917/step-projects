// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random fact to the page.
 */
function addRandomFact() {
  const facts =
      ['I used to be a competitive golfer.', 'I love reading books.', 'My favorite foods are eggs and tomatoes.', 'I can\'t stand slow walkers.', 'There is a kangaroo cropped out of the image of me shown above.'];

  // Pick a random fact.
  const fact = facts[Math.floor(Math.random() * facts.length)];

  // Add it to the page.
  const factContainer = document.getElementById('fact-container');
  factContainer.innerText = fact;
}

var greetingIndex = 1;

function addGreeting() {
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];
  const images = ["berkeley.jpg", "spain.jpg", "china.jpg", "france.jpg"];
  const helloContainer = document.getElementById('hello-container');
  const helloText = document.getElementById('hello-text');
  setInterval(getNextGreeting, 2000, greetings, images, helloContainer, helloText);
}

function getNextGreeting(greetings, images, helloContainer, helloText) {
    helloText.innerHTML = greetings[greetingIndex];
    if (greetingIndex == 0) {
        helloText.style.color = "white";
    } else {
        helloText.style.color = "rgb(7, 35, 63)";
    }
    var urlstring = "url(images/";
    helloContainer.style.backgroundImage = urlstring.concat(images[greetingIndex], ")");
    greetingIndex = (greetingIndex + 1) % greetings.length;
}

function toggleCollapsible(collapsibleButton) {
    collapsibleButton.classList.toggle("active");
    var collapsedContent = collapsibleButton.nextElementSibling;
    if (collapsedContent.style.display === "block") {
      collapsedContent.style.display = "none";
    } else {
      collapsedContent.style.display = "block";
    }
}

var prevValue = 5;

function getComments() {
  const countTextBox = document.getElementsByName("display-count")[0];
  var displayCount = countTextBox.value;
  if (invalidCountValue(displayCount)) {
      prevCount = window.sessionStorage.getItem("prevDisplayCount");
      displayCount = prevCount == null ? 5 : prevCount;
      countTextBox.value = displayCount;
  } else {
      window.sessionStorage.setItem("prevDisplayCount", displayCount);
  }

  fetch('/list-comments?count=' + displayCount).then(response => response.json()).then((history) => {
    const historyContainer = document.getElementById('comment-history');
    historyContainer.innerHTML = "";
    history.forEach((comment) => {
      historyContainer.appendChild(createCommentDisplay(comment.text, comment.timestamp));
    });
  });
}

function invalidCountValue(displayCount) {
    if (displayCount == "") {
        return true;
    }
    if (displayCount < 5) {
      window.alert("Input value for comment display count out of range. Must be an integer greater than 5.");
      return true;
    }
    return false;
}

function createCommentDisplay(text, timestamp) {
  var commentDiv = document.createElement("div");
  commentDiv.style.marginRight = "30px";
  commentDiv.style.marginTop = "5px";
  commentDiv.style.padding = "20px";
  commentDiv.style.paddingTop = "10px";
  commentDiv.style.paddingBottom = "10px";
  commentDiv.style.background = "white";
  
  var textContainer = document.createElement("p");
  var commentText = document.createTextNode(text);
  textContainer.appendChild(commentText);
  textContainer.style.fontSize = "20px";
  

  var timeContainer = document.createElement("p");
  timeContainer.style.color = "gray";
  timeContainer.style.fontSize = "15px";
  var timeDiffHours = Math.floor((new Date() - timestamp) / 36e5);
  var timeText = document.createTextNode(timeDiffHours.toString() + " hours ago");
  timeContainer.appendChild(timeText);

  commentDiv.appendChild(timeContainer)
  commentDiv.appendChild(textContainer);
  return commentDiv;
}
