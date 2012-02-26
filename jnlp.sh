#!/bin/sh

VERSION="0.0.0-SNAPSHOT"

# A total hack to make the webstart site.
rm -Rf classes/
lein uberjar
cp regenemies-$VERSION-standalone.jar jnlp/
jarsigner jnlp/regenemies-$VERSION-standalone.jar regenemies
