# tongue

Translate you Android UI to user native language in 3 lines.
Library makes possible translation of UI without extra efforts.

# State

[![Build Status](https://travis-ci.org/OleksandrKucherenko/tongue.svg)](travisci)
[![Coverage Status](https://coveralls.io/repos/OleksandrKucherenko/tongue/badge.svg?branch=master)](coveralls)
[![Coverity Scan Build Status](https://scan.coverity.com/projects/4875/badge.svg)](coverity)
[![Download](https://api.bintray.com/packages/kucherenko-alex/android/com.artfulbits%3Atongue/images/download.svg)](bintray)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](gitter)

Active development, started at: 2015-04-19

# Goals

* Minimalistic code
* Attaching to existing project with minimal changes (1 line for enabling)
* 'Cheap' localization of the application over Google Translate
* Collect suggested by user translations (crowd fund translation)
* Support of advanced scenarios:
    * Custom controls/views
    * Custom inflaters
    * Another localization provider
    * Another library for Web API calls (right now used Volley)
* Covered fully by tests

# Configure the Project

## Identifying your application to Google

If you haven't done so already, create your project's API key by following these steps:

1. Go to the Google [Developers Console](devconsole).
2. Select a project, or create a new one.
3. In the sidebar on the left, expand APIs & auth. Next, click APIs. Select the Enabled APIs link in the API section to see a list of all your enabled APIs. Make sure that the Google Translate API is on the list of enabled APIs. If you have not enabled it, select the API from the list of APIs, then select the Enable API button for the API.
4. In the sidebar on the left, select **Credentials**.
  * **Public API access**: A request that does not provide an OAuth 2.0 token must send an API key. The key identifies your project and provides API access, quota, and reports. To create an API key, click Create new Key and select the appropriate key type. Enter the additional information required for that key type and click Create.

To keep your API keys secure, follow the [best practices for securely using API keys](best-key).

Once created, your API key can be found in the appropriate table on the Credentials page.

[More Details](auth)

## Embed API Key into AndroidManifest.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">
    <application>
        <!-- Google Translate API Key : Public Key -->
        <meta-data
            android:name="com.google.translate.api.key"
            android:value="...place key here..."/>
    </application>
</manifest>

```

# License

    Copyright 2015 Oleksandr Kucherenko
    Copyright 2005-2015 ArtfulBits, Inc. (http://www.artfulbits.com)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[devconsole]: https://console.developers.google.com
[auth]: https://cloud.google.com/translate/v2/using_rest#auth
[best-key]: https://developers.google.com/console/help/api-key-best-practices
[travisci]: https://travis-ci.org/OleksandrKucherenko/tongue
[coveralls]: https://coveralls.io/r/OleksandrKucherenko/meter?branch=master
[coverity]: https://scan.coverity.com/projects/4875
[bintray]: https://bintray.com/kucherenko-alex/android/com.artfulbits%3Atongue/_latestVersion
[gitter]: https://gitter.im/OleksandrKucherenko/tongue?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge
