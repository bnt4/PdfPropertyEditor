# PdfPropertyEditor - Edit PDF Metadata
- Edit Title, which is shown in most browsers/viewers
- Edit Subject, Author, Producer, Creator, Keywords, Trapped
- Change Creation & Modification Date
- Protect PDF Files with Passwords
- PDF Permission Support

![image](https://user-images.githubusercontent.com/58621956/198884349-a0acdd66-7be1-4284-9654-a2ba9bb472fb.png)


<details>
  <summary>Encryption Settings</summary>
  
![image](https://user-images.githubusercontent.com/58621956/198884178-37ba39ac-5d3c-46f0-84ee-c045bbf870cd.png)
</details>

## Tested Java Versions
- Java 8
- Java 17
- Java 18

## Backups
Everytime a file is saved, a backup of that file will be saved to your OS Temp folder.
- Windows: `%temp%\PdfPropertyEditor\backup\`
- Linux: `/tmp/PdfPropertyEditor/backup/`

## Config
- Windows: `%APPDATA%\PdfPropertyEditor\config.ini`
- Linux: `/home/USER/.config/PdfPropertyEditor/config.ini`

### Themes
- **FlatIntelliJLaf** - Class: `com.formdev.flatlaf.FlatIntelliJLaf` (Default)
- **FlatDarculaLaf** - Class: `com.formdev.flatlaf.FlatDarculaLaf`
- **Windows Default** - Class: `com.sun.java.swing.plaf.windows.WindowsLookAndFeel`
- **FlatLightLaf** - Class: `com.formdev.flatlaf.FlatLightLaf`
- **FlatDarkLaf** - Class: `com.formdev.flatlaf.FlatDarkLaf`

To change the theme, set the `theme_class` property in the config to the given classes above.

# License
This code is under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).
