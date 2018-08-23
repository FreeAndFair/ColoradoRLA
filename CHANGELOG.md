# Changes

Releases correspond to Git tags. Changes between releases are described below with the following tags indicating the components affected:
- `UI` refers the user interface
- `API` refers to the server component
- `INFRA` referes to infrastructure changes such as Docker, HTTPD configuration, etc.

## 1.4.0 - In Progress

The sprint ending on 8/29/2018

- [API - Log on phantom CVR creation][pr33]
- [UI - Fix a bug where standardized contests would sometimes not display on first run][pr34]

## 1.3.4 - Bugfixes

- [UI - Using MIME type is unreliable][pr29]

## 1.3.3 - Bugfixes

The sprint beginning on 7/18/2018 and ending on 7/31/2018

Fixes bugs from 1.3.0 and 1.3.2 releases

- unauditable contests becoming auditable
- large decimal in risk limit display
- fixed issue where no canonical contests file is uploaded
- removed 0 from range of random numbers

## 1.3.2 - New Features

The sprint beginning on 7/18/2018 and ending on 7/31/2018

- Random ballot selection is driven by ballot manifests (#23) see the
  [wiki](https://github.com/democracyworks/ColoradoRLA/wiki/Random-Number-flow)
  for details. Some attributes still come from the CVR data(imprintedID for
  one). We hope to change that in the future.
- Add support for standardizing contest names (#24 and #25)


## 1.3.1 - Bugfixes

- [API - Fix the "freeze" bug preventing Alamosa from completing their audit](https://github.com/democracyworks/ColoradoRLA/pull/17)
- [UI - Challenge response separates coordinates](https://github.com/democracyworks/ColoradoRLA/pull/18)

## 1.3.0

This represents the two sprints beginning on 6/19/2018 and ending on 7/17/2018
- [Documentation Updates][pr15]
- [UI - Lowercase username parameter to login2F][pr13]
- [remove parameter name constraint][pr11]
- [INFRA - Add support for running in virtual environments][pr10]
- [UI/API - Incorporate CDOS changes][pr9] made between 2017 F&F delivery and Colorado's 2017 General Election
- [UI - All audit info editable][pr8]
- [UI - Add selected contests to review page][pr5]
- [UI - Use a single input field for 2FA challenge][pr4]
- [UI - Usernames need to be lowercase][pr3]
- [API - Ballot Manifest can use strings for Batch IDs][pr2]
- [API - Ballot Manifest objects store their effective start / end sequence numbers][pr1]

## 1.1.0.3

This is [FreeAndFair's most recent tag][1.1.0.3].

[1.1.0.3]: https://github.com/FreeAndFair/ColoradoRLA/tree/v1.1.0.3
[fork]: https://github.com/FreeAndFair/ColoradoRLA/commit/fbbc9aba46c4db4b9c7349a855397a27439d2a5b
[first-commit]: https://github.com/democracyworks/ColoradoRLA/commit/6ce7a45540ccad35ddef85bb38b3fd31d11368ad
[pr1]: https://github.com/democracyworks/ColoradoRLA/pull/1
[pr2]: https://github.com/democracyworks/ColoradoRLA/pull/2
[pr3]: https://github.com/democracyworks/ColoradoRLA/pull/3
[pr4]: https://github.com/democracyworks/ColoradoRLA/pull/4
[pr5]: https://github.com/democracyworks/ColoradoRLA/pull/5
[pr8]: https://github.com/democracyworks/ColoradoRLA/pull/8
[pr9]: https://github.com/democracyworks/ColoradoRLA/pull/9
[pr10]: https://github.com/democracyworks/ColoradoRLA/pull/10
[pr11]: https://github.com/democracyworks/ColoradoRLA/pull/11
[pr13]: https://github.com/democracyworks/ColoradoRLA/pull/13
[pr15]: https://github.com/democracyworks/ColoradoRLA/pull/15
[pr29]: https://github.com/democracyworks/ColoradoRLA/pull/29
[pr33]: https://github.com/democracyworks/ColoradoRLA/pull/33
[pr34]: https://github.com/democracyworks/ColoradoRLA/pull/34
