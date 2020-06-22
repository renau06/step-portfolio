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
 * Adds a random greeting to the page.
 */

var memoji= setInterval(function generateImage(){
    const images= 
    ["/images/memoji1.jpg","/images/memoji2.jpg","/images/memoji3.jpg","/images/memoji4.jpg","/images/memoji5.jpg","/images/memoji6.jpg"];

    const image= images[Math.floor(Math.random() * images.length)];
    const imageContainer= document.getElementById('image-container');
    imageContainer.innerHTML=`
    <img src="${image}">`;
}, 3000);


init();

