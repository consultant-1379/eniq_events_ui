/**
 * Utility Javascript Methods
 * for use with dynamically adding the
 * css style sheets based on theme and image manipulations
 * based on browser resizing
 *
 * @author eendmcm
 * @since Oct 2010
 */

    //Global vars moved from EniqEventsUI\SessionTimeOutManagment.js
var sessionNextTimeToExpireEpochDateTime = 0;
var dateTimeNow;
var epochDateTimeNow;

/* eendmcm - NOTE: MOVED FROM EniqEventsUI\SessionTimeOutManagment.js as this is a compiled folder */
//Handles session timeout interval. The function updates the session timeout time
//each time a user presses the mouse button on the page.
//If the current time has exceeded the time session set timeout time "sessionNextTimeToExpireEpochDateTime"
//then it is deemed that the session is expired and the user need to login.
function checkSessionTimeoutHasExpired() {

    dateTimeNow = new Date();
    epochDateTimeNow = dateTimeNow.getTime();

    if (sessionNextTimeToExpireEpochDateTime == 0 || epochDateTimeNow < sessionNextTimeToExpireEpochDateTime) {
        sessionNextTimeToExpireEpochDateTime = epochDateTimeNow + sessionXMLDefindedTimeOutTimeInMilliSeconds;
    }
    else {
        onLogout();
    }
}
