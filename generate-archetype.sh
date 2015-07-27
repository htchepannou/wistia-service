 #!/bin/sh

mvn clean install archetype:create-from-project

cd target/generated-sources/archetype

rm src/main/resources/archetype-resources/generate-archetype.sh
rm src/main/resources/archetype-resources/*.iml
rm src/main/resources/archetype-resources/*.md
rm -Rf src/main/resources/archetype-resources/.idea
rm -Rf src/main/resources/archetype-resources/log

cp ../../../.gitignore src/main/resources/archetype-resources
cp ../../../README-archetype.md src/main/resources/archetype-resources/README.md

mvn install

cd ../../..
