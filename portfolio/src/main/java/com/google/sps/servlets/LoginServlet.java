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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

@WebServlet("/login-status")
public class LoginServlet extends HttpServlet {

     public class Login{ 
        String status;
        String url;
  
        public Login(String status, String url) {
            this.status =status;
            this.url = url;
        }
    }
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    String loginStatus = Boolean.toString(userService.isUserLoggedIn());

    if (userService.isUserLoggedIn()) {
      String userEmail = userService.getCurrentUser().getEmail();
      String urlToRedirectToAfterUserLogsOut = "/contact.html";
      String logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);
      Login login = new Login(loginStatus,logoutUrl);
      String json = convertToJsonUsingGson(login);
      response.setContentType("application/json;");
      response.getWriter().println(json);
    } 
    else {
      String urlToRedirectToAfterUserLogsIn = "/contact.html";
      String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);
      Login login = new Login(loginStatus,loginUrl);
      String json = convertToJsonUsingGson(login);
      response.setContentType("application/json;");
      response.getWriter().println(json);
    }
   
  }

   private String convertToJsonUsingGson(Login login) {
    Gson gson = new Gson();
    String json = gson.toJson(login);
    return json;
  }
}