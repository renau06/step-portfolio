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

var memoji = setInterval(function(){
    const images = 
    ["/images/memoji1.jpg","/images/memoji2.jpg","/images/memoji3.jpg","/images/memoji4.jpg","/images/memoji5.jpg","/images/memoji6.jpg"];
    const image = images[Math.floor(Math.random() * images.length)];
    document.getElementById("memoji").src = image;
}, 3000);


var pictures = ["/images/picture1.JPG","/images/picture2.JPG","/images/picture3.JPG","/images/picture5.jpg","/images/picture6.JPG","/images/picture7.JPG","/images/picture8.jpg","/images/picture10.jpg","/images/picture11.JPG","/images/picture12.jpg","/images/picture13.jpg","/images/picture14.jpg"];

function createPictures(pictures,sectionSelector){
    let section = document.querySelector(sectionSelector);
    if(section){
        if (pictures instanceof Array){
            for(let picture of pictures){
                let image = document.createElement("img");
                image.className = "picture";
                image.src = picture;
                section.append(image);
            }
        }
    }
}



