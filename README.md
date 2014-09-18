LiveQuizzMbaas
==============

## A live quizz POC android app made with the help of two MBAAS Kinvey and Firebase.


This app was made for fun for an interactive presentation on MBAAS.
You can find this presentation here :
- slides in english : http://fr.slideshare.net/GuilhemDuch
- video in french : https://www.youtube.com/watch?v=X49o6RSnis0

To make it work you have to setup apps on :
- kinvey.com : set up an app on kinvey.com mbaas, then set up app value in kinvey.properties in assets folder of the android app.
- firebase : set up an app on firebase.com realtime mbaas and change all java references to firebase to target your new environment.


## Support
I don't plan to detail the architecture and document the code because i'm too lazy on this one ;)
Some people may find it interesting to reuse or study.
I don't  plan to provide any support neither to update it. But who knows ...
Therefore i took the time to release it as open source.
Do not hesitate to modify it, update it, or fork it.

This app uses other open source libraries :
- google http client
- gson
- guava
- butterknife
- eventbus
- action bar pull to refresh

License
-------

    Copyright 2014 Guilhem Duché

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
