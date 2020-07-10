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

package com.google.sps.servlets;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

/** Servlet that returns data of comments*/
@WebServlet("/data")
public class DataServlet extends HttpServlet {
    private final Gson gson = new Gson();

    public static class Comment { 
        private final String name; 
        private final String email;
        private final String comment;
        private final long timestamp; 
        private final long id;
  
        public Comment(String name, String email, String comment, long timestamp, long id) {
            this.name = name;
            this.email = email;
            this.comment = comment;
            this.timestamp =timestamp;
            this.id= id;
        }
    }
    
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    ArrayList<Comment> comments = new ArrayList<Comment>();
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    String numChoice = request.getParameter("num");
    String languageChoice = request.getParameter("language");
    int maxComments;
    maxComments = Integer.parseInt(numChoice);
    int i =0;
    for (Entity entity : results.asIterable()) {
        if (i < maxComments){
            long id = entity.getKey().getId();
            String name = (String) entity.getProperty("name");
            String email = (String) entity.getProperty("email");
            String comment = (String) entity.getProperty("comment");
            long timestamp = (long) entity.getProperty("timestamp");

            Translate translate = TranslateOptions.getDefaultInstance().getService();
            Translation translation =
                translate.translate(comment, Translate.TranslateOption.targetLanguage(languageChoice));
            String translatedText = translation.getTranslatedText();

            Comment userComment = new Comment(name, email, translatedText,timestamp,id);
                    comments.add(0,userComment);        
        }
        i++;
    }
    

    String json = gson.toJson(comments);
    response.setContentType("application/json;");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(json);
  }



  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.
    UserService userService = UserServiceFactory.getUserService();

    String name = getUserNickname(userService.getCurrentUser().getUserId());
    String email = userService.getCurrentUser().getEmail();
    String comment = request.getParameter("comment");
    long timestamp = System.currentTimeMillis();

    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("name", name);
    commentEntity.setProperty("email", email);
    commentEntity.setProperty("comment", comment);
    commentEntity.setProperty("timestamp", timestamp);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
    response.sendRedirect("/contact.html");
  }

//left this the same
private String getUserNickname(String id) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query =
        new Query("UserInfo")
            .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    if (entity == null) {
      return "";
    }
    String nickname = (String) entity.getProperty("nickname");
    return nickname;
  }

}



