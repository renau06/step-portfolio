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
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
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

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

    public static class Comment{ 
        private final String name; 
        private final String email;
        private final String comment;
        private final long timestamp; 
        private final long id;
        private final double score;
  
        public Comment(String name, String email, String comment, long timestamp, long id, double score) {
            this.name = name;
            this.email = email;
            this.comment = comment;
            this.timestamp =timestamp;
            this.id = id;
            this.score = score;
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
            double score = (double) entity.getProperty("score");

            Translate translate = TranslateOptions.getDefaultInstance().getService();
            Translation translation =
                translate.translate(comment, Translate.TranslateOption.targetLanguage(languageChoice));
            String translatedText = translation.getTranslatedText();
        
            Comment user_comment = new Comment(name, email, translatedText,timestamp,id, score);
                comments.add(0,user_comment);        
        }
        i++;
    }
    

    String json = convertToJsonUsingGson(comments);
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

    Document doc =
        Document.newBuilder().setContent(comment).setType(Document.Type.PLAIN_TEXT).build();
    LanguageServiceClient languageService = LanguageServiceClient.create();
    Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
    float score = sentiment.getScore();
    languageService.close();

    

    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("name", name);
    commentEntity.setProperty("email", email);
    commentEntity.setProperty("comment", comment);
    commentEntity.setProperty("timestamp", timestamp);
    commentEntity.setProperty("score",score);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
    response.sendRedirect("/contact.html");
  }

  
  private String convertToJson(ArrayList<String> messages) {
    String json = "[";
    json += "\"" + messages.get(0) + "\"";
    for (int i=1; i<messages.size(); i++){
        json += ", ";
        json += "\"" + messages.get(i) + "\"";
    }
    json += "]";
    return json;
  }

  private String convertToJsonUsingGson(ArrayList<Comment> comments) {
    Gson gson = new Gson();
    String json = gson.toJson(comments);
    return json;
  }

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








