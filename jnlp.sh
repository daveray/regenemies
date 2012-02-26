#!/bin/sh

VERSION="0.0.0-SNAPSHOT"

# A total hack to make the webstart site.
lein uberjar
mkdir jnlp/lib
cp regenemies-$VERSION-standalone.jar jnlp/lib/
jarsigner jnlp/lib/regenemies-$VERSION-standalone.jar regenemies
