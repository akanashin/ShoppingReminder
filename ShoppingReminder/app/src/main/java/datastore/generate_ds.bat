REM delete old version (if present)
rmdir /S /Q generated

REM generate files for database and content provider
java -jar android_contentprovider_generator-1.9.2-bundle.jar -i configs -o generated

REM move files to proper directory
move /Y generated\datastore\generated\provider generated
rmdir /S /Q generated\datastore