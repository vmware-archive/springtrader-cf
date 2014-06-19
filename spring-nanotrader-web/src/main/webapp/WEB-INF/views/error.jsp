<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<!DOCTYPE html>
<!--[if lt IE 7]>      <html lang="en-GB" class="no-js ie6"> <![endif]-->
<!--[if IE 7   ]>      <html lang="en-GB" class="no-js ie7"> <![endif]-->
<!--[if IE 8   ]>      <html lang="en-GB" class="no-js ie8"> <![endif]-->
<!--[if gt IE 8]><!--> <html lang="en-GB" class="no-js"> <!--<![endif]-->
<html lang="en">
    <head>
        <meta charset="utf-8">
        <title>SpringTrader :: Dashboard</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="">
        <meta name="author" content="">
        <link href="css/jquery.jqplot.min.css" rel="stylesheet">
        <link href="css/bootstrap.css" rel="stylesheet">
        <link href="css/bootstrap-responsive.css" rel="stylesheet">
        <link href="css/docs.css" rel="stylesheet">
        <link href="css/style.css" rel="stylesheet">
        <script src="js/jquery.min.js"></script>
    </head>
    <script>
      var viewMode = nano.utils.getViewPrefCookie();
      if (viewMode == 'mobileView') {
          nano.utils.setViewPrefCookie("mobileView");
          location.replace('mobile.html');
      }
      else {
          nano.utils.setViewPrefCookie("fullView");
      }
    </script>
    <body>
        <!-- The prefix "nc" stands for "nano container"-->
        <div id="nc-navbar" class="navbar-fixed-top"></div>
        <div class="container page">
			<p>The web application must be bound to a single web service (got: ${numServices} of ${type})</p>
        </div>
        <div id="nc-footer"></div>
    </body>
</html>