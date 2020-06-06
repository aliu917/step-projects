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
      ['I used to be a competitive golfer.', 
      'I love reading books.', 
      'My favorite foods are eggs and tomatoes.', 
      'I can\'t stand slow walkers.', 
      'There is a kangaroo cropped out of the image of me shown above.',
      'Pineapples can go on anything (including pizza, fried rice, korean bbq, etc.).'];

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

function refreshComments() {
  setInterval(getComments, 60000);
}

function getComments() {
  getNumComments(commentDisplayNum());
}

function getNumComments(displayCount) {
  fetch('/list-comments?count=' + displayCount).then(response => response.json()).then((history) => {
    const historyContainer = document.getElementById('comment-history');
    historyContainer.innerHTML = "";
    history.forEach((comment) => {
      historyContainer.appendChild(createCommentDisplay(comment));
    });
    const moreButton = document.getElementsByName("more-comments")[0];
    const countTextBox = document.getElementsByName("display-count")[0];
    if (displayCount > history.length) {
      moreButton.style.display = "none";
      countTextBox.value = history.length;
    } else {
      moreButton.style.display = "block";
      countTextBox.value = displayCount;
    }
  });
}

function getMoreComments() {
  var moreDisplayNum = parseInt(commentDisplayNum()) + 10;
  window.sessionStorage.setItem("prevDisplayCount", moreDisplayNum);
  getNumComments(moreDisplayNum);
}

function commentDisplayNum() {
  const countTextBox = document.getElementsByName("display-count")[0];
  var displayCount = countTextBox.value;
  if (invalidCountValue(displayCount)) {
      prevCount = window.sessionStorage.getItem("prevDisplayCount");
      displayCount = prevCount == null ? 5 : prevCount;
      countTextBox.value = displayCount;
  } else {
      window.sessionStorage.setItem("prevDisplayCount", displayCount);
  }
  return displayCount;
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

function createCommentDisplay(comment) {
  var commentDiv = document.createElement("div");
  commentDiv.classList.add('comment');
  commentDiv.style.position = "Relative";
  
  var textContainer = document.createElement("p");
  var commentText = document.createTextNode(comment.text);
  textContainer.appendChild(commentText);
  textContainer.style.fontSize = "20px";

  var nameContainer = document.createElement("p");
  nameContainer.style.fontSize = "20px";
  nameContainer.style.fontWeight = "bold";
  nameContainer.style.marginBottom = "0px";
  var nameText = document.createTextNode(comment.username);
  nameContainer.appendChild(nameText);

  var timeContainer = document.createElement("p");
  timeContainer.style.color = "gray";
  timeContainer.style.fontSize = "15px";
  timeContainer.style.marginTop = "3px";
  var timeDiff = getTimeDiff(comment.timestamp);
  var timeText = document.createTextNode(timeDiff);
  timeContainer.appendChild(timeText);

  commentDiv.appendChild(nameContainer);
  commentDiv.appendChild(timeContainer);
  commentDiv.appendChild(textContainer);

  if (comment.currentUserComment) {
	var deleteButton = document.createElement("button");
    deleteButton.classList.add('text-button');
    deleteButton.style.position = "absolute";
    deleteButton.style.right = "5px";
    deleteButton.style.top = "5px";
    deleteButton.innerText = "Delete";
    deleteButton.addEventListener('click', () => {
      deleteComment(comment);
      commentDiv.remove();
    });
    commentDiv.appendChild(deleteButton);
  }

  return commentDiv;
}

function deleteComment(comment) {
  const params = new URLSearchParams();
  params.append('id', comment.id);
  fetch('/delete-comment', {method: 'POST', body: params});
}

function getTimeDiff(timestamp) {
  var msDiff = (new Date()).getTime() - timestamp;
  if (msDiff / 60000 < 1) {  //Less than 1 minute ago --> Just now
    return "Just now";
  } else if (msDiff / 3600000 < 1) {   //Less than an hour ago --> min units
    var minuteDiff = Math.floor(msDiff / 60000);
    if (minuteDiff == 1) {
      return "1 min ago";
    } else {
      return minuteDiff.toString() + " mins ago";
    }
  } else if (msDiff / 86400000 < 1) {   //Less than one day ago --> hour units
    var hourDiff = Math.floor((new Date() - timestamp) / 36e5);
    if (hourDiff == 1) {
      return "1 hour ago";
    } else {
      return hourDiff.toString() + " hours ago";
    }
  } else {   //More than a day ago --> use date
    date = new Date(timestamp);
    return date.toLocaleDateString();
  }
}

function getAuthInput(input) {
  fetch(input).then((response) => response.text()).then((displayText) => {
    var container = document.getElementsByName("authcheck")[0];
    container.innerHTML = displayText;
  });
}

function getAuth() {
  getAuthInput("/auth");
}

function showGuestForm() {
  getAuthInput("/auth?guest=true");
}

function initMap() {
  var map = new google.maps.Map(document.getElementById('map'), {
    center: {lat: -37.815018, lng: 144.946014},
    zoom: 8
  });
  for (var placeName in places) {
    var infowindow = new google.maps.InfoWindow({content: placeName});
    var marker = new google.maps.Marker({position: places[placeName], map: map, title: placeName, info: infowindow});
    marker.addListener('click', function() {
      if (isInfoWindowOpen(this.info)) {
        this.info.close(map, this);
      } else {
        this.info.open(map, this);
      }
    });
    markers[placeName] = marker;
  }
}

function flipInfo(place) {
  var marker = markers[place];
  marker.info.open(map, marker);
}

function isInfoWindowOpen(infoWindow) {
  var map = infoWindow.getMap();
  return (map !== null && typeof map !== "undefined");
}

String.format = function() {
  var s = arguments[0];
  for (var i = 0; i < arguments.length - 1; i += 1) {
    var reg = new RegExp('\\{' + i + '\\}', 'gm');
    s = s.replace(reg, arguments[i + 1]);
  }
  return s;
};

function createDestinations() {
  var container = document.getElementsByName("destinations")[0];
  var htmlFlipContainer = document.createElement("grid-container");
  fetch('files/dest.txt').then(response => response.text()).then((html) => {
    for (var dest in destinations) {
      var name = dest;
      var attr = destinations[dest];
      var img = attr["img"];
      var title = attr["title"];
      var time = attr["time"];
      var htmlScript = (attr["link"] != null) ? "<a href='" + attr["link"] + ".html'>" + html + "</a>" : html;
      var backContent = (attr["back"] != null) ? attr["back"] : "I might update this with info one day";
      var destDiv = document.createElement("div");
      destDiv.innerHTML = String.format(htmlScript, name, img, title, time, backContent);
      htmlFlipContainer.appendChild(destDiv.firstChild);
    }
    container.appendChild(htmlFlipContainer);
  })
}

function makeSlides() {
  var container = document.getElementsByName("slides")[0];
  var dots_container = document.getElementsByName("bottom-dots")[0];
  fetch('files/slides.txt').then(response => response.text()).then((html) => {
    var index = 1;
    var bottom_dots_html = "";
    for (var img in slideImages) {
      var destDiv = document.createElement("div");
      destDiv.innerHTML = String.format(html, img, slideImages[img]);
      container.appendChild(destDiv.firstChild);
      bottom_dots_html = bottom_dots_html + "<span class=\"dot\" onclick=\"currentSlide(" + index + ")\"></span>";
      index ++;
    }
    dots_container.innerHTML = bottom_dots_html;
    showSlides(1);
  })
}

var slideIndex = 1;

// Next/previous controls
function plusSlides(n) {
  showSlides(slideIndex += n);
}

// Thumbnail image controls
function currentSlide(n) {
  showSlides(slideIndex = n);
}

function showSlides(n) {
  var i;
  var slides = document.getElementsByClassName("mySlides");
  var dots = document.getElementsByClassName("dot");
  if (n > slides.length) {slideIndex = 1}
  if (n < 1) {slideIndex = slides.length}
  for (i = 0; i < slides.length; i++) {
      slides[i].style.display = "none";
  }
  for (i = 0; i < dots.length; i++) {
      dots[i].className = dots[i].className.replace(" active", "");
  }
  slides[slideIndex-1].style.display = "block";
  dots[slideIndex-1].className += " active";
}
