<%@ page import="java.io.BufferedReader"
        %><%@ page import="java.io.DataOutputStream"
        %><%@ page import="java.io.InputStreamReader"
        %><%@ page import="java.net.CookieHandler"
        %><%@ page import="java.net.CookieManager"
        %><%@ page import="java.net.HttpURLConnection"
        %><%@ page import="java.net.URI"
        %><%@ page import="java.net.URL"
        %><%@ page import="java.net.URLEncoder"
        %><%@ page import="java.util.Enumeration"
        %><%@ page import="java.util.Map"
        %><%@ page import="static java.net.CookiePolicy.ACCEPT_ALL"
        %><%@ page import="static com.ericsson.eniq.events.ui.server.config.ApplicationConfigManager.*"
        %><jsp:useBean id="credentialsHolder"
                       class="com.ericsson.eniq.events.common.server.CredentialsHolder"
                       scope="session"></jsp:useBean><%


    String csvData = request.getParameter("csvData");

    if (csvData != null && !csvData.isEmpty()) {
        response.setContentType("application/csv");
        response.setHeader("Content-disposition", "attachment; filename=export.csv");
        out.flush();
        out.print(csvData);
        out.close();
        return;
    }
    String servicesURI = (String) session.getAttribute(ENIQ_EVENTS_SERVICES_URI);

    String urlParam = request.getParameter("url");
    StringBuilder builder = new StringBuilder(urlParam);
    builder.append("?");

    String userName = null;

    Map paramMap = request.getParameterMap();
    Enumeration names = request.getParameterNames();
    while (names.hasMoreElements()) {
        String name = (String)names.nextElement();
        if (!name.equalsIgnoreCase("url")) {
            String value =((String[]) paramMap.get(name))[0];
            if (name.equals("userName")) {
                userName = value;
                continue;
            }
            builder.append("&");
            builder.append(name);
            builder.append("=");
            builder.append(URLEncoder.encode(value,"UTF-8"));
        }
    }

    URL url;
    HttpURLConnection con;

    if (userName != null && credentialsHolder.getUserPassword(userName) != null) {
        String authenticatedUser = credentialsHolder.getAuthenticatedUser();
        if (authenticatedUser == null) {
            response.setContentType("application/csv");
            response.setHeader("Content-disposition", "attachment; filename=export.csv");
            CookieHandler.setDefault(new CookieManager(null, ACCEPT_ALL));
            url = new URL(servicesURI + "j_security_check");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setInstanceFollowRedirects(false);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            String urlParameters = "j_username=" + URLEncoder.encode(userName, "UTF-8") +
                    "&j_password=" +URLEncoder.encode(credentialsHolder.getUserPassword(userName), "UTF-8");

            con.setRequestProperty("Content-Length", "" + Integer.toString
                    (urlParameters.getBytes().length));
            con.setRequestProperty("Content-Language", "en-US");

            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);

            con.connect();

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            if (con.getResponseCode() == 302) { // Moved temporarily
                authenticatedUser = userName;
                credentialsHolder.setAuthenticated(userName);
            }
            else{
                credentialsHolder.resetAuthenticated(userName);
            }
            con.getContent(); // Just consume the input stream
            con.disconnect();
        }

        if (userName.equals(authenticatedUser)) {
            response.setContentType("application/csv");
            response.setHeader("Content-disposition", "attachment; filename=export.csv");
            final String CONTACT_SYS_ADMIN ="If issue persists contact System Administrator.";
            final String SERVER_ERROR_CONTACT_SYS_ADMIN = "server error. " +CONTACT_SYS_ADMIN;
            final String SERVER_ERROR_RESOURCE_NOT_FOUND = "server error, resource not found.  " +CONTACT_SYS_ADMIN;
            final String UNEXPECTED_SERVER_RESPONSE = "Unexpected server response. Please login again.  " +CONTACT_SYS_ADMIN;
            url = new URL(servicesURI + builder.toString());
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(0); // Infinite timeout
            con.setRequestProperty("Content-Type", "application/csv");
            con.setRequestProperty("Accept", "application/csv");
            con.setDoInput(true);
            con.connect();

            final int responseCode= con.getResponseCode();

            if (responseCode == 200) { // ok Success

                BufferedReader in = new BufferedReader(new InputStreamReader
                        (con.getInputStream()));
                String inputLine;
                int i = 0;
                int lineNo=0;
                boolean  isCsvCorrupt= false;
                while ((inputLine = in.readLine()) != null) {
                    if(lineNo<10){
                        isCsvCorrupt=inputLine.contains("ENIQ Events Services Login Page");
                        lineNo++;
                    }
                    if(!isCsvCorrupt){
                        if(i % 200 == 0) {
                            out.flush();
                        }
                        out.println(inputLine);
                        i++;
                    } else{
                        out.clearBuffer();
                        out.println(UNEXPECTED_SERVER_RESPONSE);
                        break;
                    }
                }
                out.close();
                in.close();
            }   else if(responseCode == 503 ){    //503
                out.println(responseCode + " " + SERVER_ERROR_CONTACT_SYS_ADMIN);
            }   else if(responseCode == 500 ){    //500
                out.println(responseCode + " " + SERVER_ERROR_CONTACT_SYS_ADMIN);
            }   else if(responseCode == 404 ){  //404
                out.println(responseCode + " " + SERVER_ERROR_RESOURCE_NOT_FOUND);
            }else{    //anything else!    we have only encountered "0" or "Aborted", as a result of glassfish restarting in terms of unexpected
                out.println(UNEXPECTED_SERVER_RESPONSE);
            }
            con.disconnect();
        }else{
            out.println("Your session has expired please log in again, please contact the System Administrator if issue persists.");
        }

    }
%>