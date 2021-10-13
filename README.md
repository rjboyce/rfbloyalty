# Volunteer Kind (Charity Personnel and Event Manager)

[![CircleCI](https://circleci.com/gh/rjboyce/volunteerkind/tree/angular12.svg?style=svg)](https://circleci.com/gh/rjboyce/volunteerkind/tree/angular12)

This application was generated using JHipster 7.2.0, you can find documentation and help at [https://www.jhipster.tech/documentation-archive/v7.2.0](https://www.jhipster.tech/documentation-archive/v7.2.0).
Jhipster generated code has been greatly modified to accommodate application needs.

This application is based on a project at the end of the Udemy course [Angular 4 Java Developers](https://www.udemy.com/course/angular-4-java-developers/?src=sac&kw=angular+4),
but with many changes and upgrades to support current technologies and to meet the needs of the Volunteer Kind application. Additional features include:

```
- Keycloak Integration (OAuth 2.0 and OpenID)
- Data Model Redesign
- Sophisticated Volunteer Registration and Event Sign In/Out
- Advanced Administration Control (Authorities and User Activation)
- User Account Control (In Progress)
- Visual Changes (Ongoing)
- Sophisticated Search and Data Entry (In Progress)
- User Password Change from Frontend (Upcoming)
- Functionality to Support Public hosting, Including a Dedicated Volunteer Finder (Upcoming)
- Volunteer Referral System with Registration Rejection Functionality (Future Improvement)
- Future Plans to Improve Internationalization and to Include Profile Image Selection (Future Improvement)

```

You may fork this repository and run on your local machine or check out my demo [https://www.vkind.site](https://www.vkind.site).

`Running this Application locally requires Node.js (latest LTS) and the most recent JDK 11`

To run the application, execute the following (dependent services are on public servers and thus require an accessible internet connection):

```
./mvnw
```

If you have forked this code to your local machine, the application is accessible at http://localhost:8080.

```
Using the Application:

You may login with User: 'admin', Password: 'admin'

Some users and events are pre-created for demonstration purposes, however, event dates will need to be
manually changed and users signed up for specific events will need to be 'approved' by having sign in/out
codes in order to demonstrate those capabilities:

1. Simply change any one of the event dates to the current day ('Events' option under 'Organization' menu)
and ensure a user exists with a login code for that event.  For admin, you only need to worry about
changing the date for the event 'Social Seekers', as that user is already preset for that event.  In order
to test for the current day you would simply change the date of the event to the current day.  The sign
in/out code for the logged in user is viewable under the 'Account' menu, under the 'View Registered Events'
option.

2. You can check for user registration (as an administrator) for specific events and edit entries via
'Event Attendances' option under the 'Organization' menu (the event name will be listed with each record)
and click on the 'Edit' button.  You will be taken to the edit page which will show if the personnel
requirement has been met, and if not the 'Generate' button (for the code) will be available to use. The
presence of the code is the 'approval' of the volunteer and may now proceed to sign in at the time of the
event.

3. The event listing for the current day (which links to the corresponding sign in/out page) is accessible
via the event Organizer's homepage (administrators also have access).  Just follow the link and click the
'Sign In/Out' button to go to the event sign in/out page.

In addition:

-- Newly created (or registered) users of the application will need to edit their profile and set a
location in order to be able to register for events, all found under the 'Account' menu.

-- The 'Event Suggestions' option is how a user will register for an event and users may view their
registrations via the 'View Registered Events' menu.

-- Items under the 'Organization' menu are the entities of the application and may be edited
independently, but be warned that removing events currently will automatically remove all event
attendances.  This will change in the future to support user tracking.  Also, deleting a location
will simply remove that location from associated users.  Users are otherwise left intact.

```
