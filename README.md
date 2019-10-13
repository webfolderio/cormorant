![WebFolder](https://raw.githubusercontent.com/webfolderio/cormorant/master/cormorant.png)

__cormorant__ is an open source OpenStack Swift compatible object storage server released under MIT.

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/webfolderio/cormorant/blob/master/LICENSE)
[![circleci](https://img.shields.io/circleci/project/github/webfolderio/cormorant.svg?label=linux)](https://circleci.com/gh/webfolderio/cormorant)
[![AppVeyor](https://img.shields.io/appveyor/ci/WebFolder/cormorant.svg?label=windows)](https://ci.appveyor.com/project/WebFolder/cormorant)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/d6af9f5df82d4568ba29ea6fceb9d605)](https://www.codacy.com/app/WebFolder/cormorant?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=webfolderio/cormorant&amp;utm_campaign=Badge_Grade)
[![Coverage Status](https://coveralls.io/repos/github/webfolderio/cormorant/badge.svg?branch=master)](https://coveralls.io/github/webfolderio/cormorant?branch=master)

### Features Support

The cormorant API is a RESTful, HTTP-based API that is compatible with swift. Using this API, you can:

* Create a container (PUT container)
* List the containers you own (GET account)
* List account metadata (HEAD account)
* Check a container's metadata (HEAD container)
* List the objects in a container (GET container)
* Delete a container as long as the container don’t have any objects in it (DELETE container)
* Store an object in a container (PUT object)
* Create a directory in a container (PUT object)
* Retrieve custom metadata for an object (HEAD object)
* Copy an object (COPY object)
* Retrieve an object (GET object)
* Delete an object (DELETE object)
* Create Large (static & dynamic) Object

### Roadmap
* ACLs
* Static Website
* Expiring Objects
* Object Versioning
* CORS

### Supported Clients

* [CloudBerry Explorer for OpenStack](https://www.cloudberrylab.com/explorer/openstack.aspx)
* [rclone](https://rclone.org/)
* [restic](https://restic.github.io/)

### Supported Client Libraries (SDKs)
__Java__
* [Apache jclouds](https://jclouds.apache.org/)
* [Javaswift joss](https://github.com/javaswift/joss)

__Go__
* [ncw/swift](https://github.com/ncw/swift)

### License
cormorant is licensed as [MIT](https://github.com/webfolderio/cormorant/blob/master/LICENSE) software.

### Supported Java Versions

Oracle & OpenJDK Java 8 & 9.

### Supported Platforms
cormorant has been tested under Windows and Ubuntu, but should work on any platform where a Java available.

### How it is tested
cormorant is regularly built and tested on [circleci](https://circleci.com/gh/webfolderio/cormorant) and [AppVeyor](https://ci.appveyor.com/project/WebFolder/cormorant).

### Author
[WebFolder OÜ](https://webfolder.io)
