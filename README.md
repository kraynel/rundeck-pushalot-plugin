Pushalot Notification Plugin for Rundeck
========================================

This plugin provides the ability to send Rundeck start/stop/failure notifications to mobile devices using the [Pushalot][pushalot] app. 

Installation
------------

Copy the [rundeck-pushalot-notification-plugin-<version>.jar][latest_release] to the libext/ directory for Rundeck.


Configuration
-------------

Configuration for this plugin is relatively straightforward. You can specify multiple tokens by separating them with commas.

1. First, [login to Pushalot][pushalot].
2. From the [pushalot Apps+Tokens page][pushalot_app_tokens], create a new application for the plugin and copy the application API key once complete.
3. Update the [project.properties][rundeck_project_properties] file for each project you wish to allow Pushalot notifications from.  To configure notifications for all projects, update [framework.properties][rundeck_framework_properties] file. You can also configure an Authorization API Token when you enable the instance (job) notifications properties.

*TIP:  The [new rundeck logo][rundeck_icon] works well as the icon when creating the app in Pushalot.*

### project.properties

    project.plugin.Notification.Pushalot.authorizationApiToken=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

### framework.properties

    framework.plugin.Notification.Pushalot.authorizationApiToken=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

Usage
-----

Check the Pushalot box when specifying notifications for a job.


Credits
-------

JR Bing wrote the same kind of code for [Pushover][pushover-plugin], which is available only on Android and iOS. I believe we needed a Windows (Phone) version!

[pushalot]: https://pushalot.com "Pushalot"
[pushalot_app_tokens]: https://pushalot.com/manager/authorizations
[rundeck_icon]: https://raw2.github.com/dtolabs/rundeck/rundeck2/rundeckapp/web-app/images/rundeck2-icon-256.png "Rundeck Icon"
[rundeck_project_properties]: http://rundeck.org/docs/administration/configuration.html#project.properties
[rundeck_framework_properties]: http://rundeck.org/docs/administration/configuration.html#framework.properties
[pushover-plugin]: https://github.com/jrbing/pushover-notification-plugin
[latest_release]: https://github.com/kraynel/rundeck-pushalot-plugin/releases/download/v1.0.0/rundeck-pushalot-notification-plugin-1.0.0.jar
