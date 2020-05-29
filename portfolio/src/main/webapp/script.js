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

function getData() {
    fetch('/data').then(response => response.json()).then((data) => {
        document.getElementById('fact-container').innerText = data;
    });
}

function getComments() {
  fetch('/list-comments').then(response => response.json()).then((history) => {
    const historyContainer = document.getElementById('comment-history');
    history.forEach((comment) => {
      historyContainer.appendChild(createListElement(comment));
    });
  });
}

function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}
