#!/bin/sh

cp ./app/src/main/java/com/egornemov/polyglotgame/domain/Data.kt ~/DummyData.kt
cp ~/Data.kt ./app/src/main/java/com/egornemov/polyglotgame/domain/

cp ./app/src/google-services.json ~/dummy-google-services.json
cp ~/google-services.json ./app/src/
