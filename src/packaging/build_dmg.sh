cd /Users/julian/Desktop/play

nohup /Users/julian/Desktop/play/Java/jdk-19.0.1.jdk/Contents/Home/bin/jpackage\
  --name  "Constellatio"\
  --icon resource/Application.icns \
  --dest /Users/julian/Desktop/play\
  --main-jar rakhuba.jar\
  --main-class launcher.Napp\
  --input lib\
  --app-version "1.0"\
  
  
  --type dmg\
  --verbose\
  --resource-dir resource\
  --java-options '--enable-preview' \
  --module-path  /Users/julian/Desktop/play/Java/javafx-jmods-19.0.2 \
  --file-associations resource/FAconstallation.properties \
  --add-modules javafx.controls,java.sql,java.naming,java.management,java.security.jgss &






#  --temp /Users/julian/Desktop/play/tmp \
#  --mac-sign \ iCalc-0.0.1-SNAPSHOT.jar
# pkgutil --expand to extract pkg to a folder and you can modify
# pkgutil --flatten to make a new one.
#  --mac-package-name rakhuba_jrg \
#   --temp tempz\ --temp directory
#   --main-jar /Users/julian/Desktop/play/rakhuba.jar\
#  --add-launcher MyApp1=MLAppArgs1.properties \
#  --arguments "Hello World" \
