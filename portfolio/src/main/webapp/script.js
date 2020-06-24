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

var memoji = setInterval(function() {
    const images = 
    ["/images/memoji1.jpg","/images/memoji2.jpg","/images/memoji3.jpg","/images/memoji4.jpg","/images/memoji5.jpg","/images/memoji6.jpg"];

    const image = images[Math.floor(Math.random() * images.length)];
    const imageContainer = document.getElementById('image-container');
    imageContainer.innerHTML=`
    <img src="${image}">`;
}, 3000);


var pictures = ["/images/picture1.JPG","/images/picture2.JPG","/images/picture3.JPG","/images/picture5.jpg","/images/picture6.JPG","/images/picture7.JPG","/images/picture8.jpg","/images/picture10.jpg","/images/picture11.JPG","/images/picture12.jpg","/images/picture13.jpg","/images/picture14.jpg"];

function createPictures(pictures,sectionSelector){
    let section = document.querySelector(sectionSelector);
    if(section){
        if (pictures instanceof Array){
            for(let picture of pictures){
                let image = document.createElement("img");
                image.className= "picture";
                image.src = picture;
                section.append(image);
            }
        }
    }
}

var projects = [
    {
        title: "Book Club App Wireframe Prototype",
        image: "/images/bookclubappsample.png",
        text: "I created this wireframe prototype of a fictional online book club app. I used Balsamiq to create this wireframe. The wireframe implements features I believed were necessary for a book club app including the ability to search books, add books to your reading lists, rate books, and read books.",
        link: "https://balsamiq.cloud/soancio/pgyhw3l/r2278"
    },

    {
        title: "Mobile Music Player Application",
        image:"/images/musicplayersample.png",
        text:"I recently created a mobile music application using HTML, CSS, and Javascript. This was one of my first projects involving HTML and CSS. I did this project for a class, however, I plan to continue working on it to add more of my own style and creativity.",
        link:"/projects/musicplayer/albums.html"
    },

    {
        title:"Take Home Pediatrics Podcast",
        image:"/images/podcastsample.png",
        text:"I am currently working for a local pediatrician who has recently started a podcast. I have created the logo and composed the soundtrack for Take Home Pediatrics: The Podcast. Now, I am working on designing the website. My aim is to make it capitvating and education, in order to draw users to listen to the podcast. Feel free to take a look at my work in progress!",
        link:"/projects/podcast/index.html"
    },
    
    {
        title:"Northeast Pediatrics Website",
        image:"/images/northeastsample.png",
        text:"Northeast Pediatrics is my local pediatrician office. In high school, I assisted in revamping their website. I worked alongside a web developer to create a new, energetic design for the Northeast Pediatrics website to increase engagement.",
        link:"https://northeastkids.webs.com/"
    }
]

function createProjects(projects, sectionSelector){
    let section = document.querySelector(sectionSelector);
    if(section){
        if (projects instanceof Array){
            for(let project of projects){
                let html = `
               <div> <a href = "${project.link}"><img src = "${project.image}" class= "image" alt="Image art"></a> </div>
               <a class="project-title" href = "${project.link}"> <div> ${project.title}</div> <a>
               <div class="project-text">${project.text}</div>`;

                let container = document.createElement("div");
                container.className="project-container";
                container.innerHTML = html;
                section.append(container);
            }
        }
    }
    

}

var socials = [
    {
        image:"/images/linkedin.png",
        link:"https://www.linkedin.com/in/rena-upadhyay-6b8b36173"
    },
    {
        image:"/images/github.png",
        link:"https://github.com/renau06"
    },
    {
        image:"/images/facebook.png",
        link:"https://www.facebook.com/people/Rena-Upadhyay/100005326726445"
    },
    {
        image:"/images/instagram.png",
        link:"https://www.instagram.com/renaupadhyay/"
    }
]

function createSocials(socials, sectionSelector){
    let section = document.querySelector(sectionSelector);
    if(section){
        if (socials instanceof Array){
            for(let social of socials){
                let container = document.createElement("div");
                container.className = "social";
                let link = document.createElement("a");
                link.href = social.link;
                let image = document.createElement("img");
                image.className= "social-image";
                image.src = social.image;
                link.append(image)
                container.append(link);
                section.append(container);
            }
        }
    }
    

}





