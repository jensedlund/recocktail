# Most of the information below is Deprecated. Everything is now under reconstruction.


# Cocktail, a dynamic soundscape engine

## Concepts
- **Snippet**, a unique sound clip, either a part of or the full duration of a
single sound file. A snippet is searchable through tags.
- **SnippetSet**, a named collection of Snippets.

## Basic user guide
With backend running, using Chrome browser, navigate to webpage  
`<ip-adress of server>:4567/index.html`  
Here you can search for snippets using tags. Every search creates a new
snippet set on the backend, which will also be visible in frontend. It
is good practice to rename snippet sets to something else than the default
creation time.  
To listen to a snippet set, use the download button. The selected snippet set
will be added to the collection of sound sets, which is snippet sets and their
corresponding sound binaries. Start, stop and sound parameters operate on the specific
sound set selected in drop down. It is possible, by design, to play several
sound sets simultaneously.

## Deploying the application.
1. Server need to have the following software installed.
  - mysql server.
  - sox for sound conversion.
  - ffmpeg for sound from video extraction.
1. Clone code to server:  
`git clone https://github.com/jensedlund/recocktail.git`
1. Create a new sql database if not available. A mysql script to create
the necessary tables is available under src:  
"src/main/java/cocktail/db_access/dbscript.sql"  
1. Edit code
  - Add a creds.txt file to directory  
  "src/main/java/cocktail/db_access/"  
  The file should be formatted look like this, without trailing newlines:
```
username:<sql server user>  
password:<sql server password>
dbname: <sql database name>  
ip-adress:<sql database ip>
```
  - Add the correct path to sox and ffmpeg in file
  ```
  src/main/java/cocktail/sound_processing/SoundExtractorImpl.java
  ```
  - Add correct ip address to variable serverUrl in file
  ```
  src/main/web/main.js
  ```
1. Run the application from src directory with the command:  
`gradle run`

## Suggested features/roadmap
This is a list of features and shortcomings that would greatly improve
Cocktail if fixed/added.

### Deployment
- Add instructions on how to deploy as a service.
- Create a script or change the source code so that no direct edits are necessary
when swapping server.

### Frontend
- The webpage needs a lot more work to be user friendly. As it is now it is
solely a functional demo.
- Snippet sets should only visible per selected user.
- Local caching between sessions.
- Mixing two sound sets left to right.
- Delete specific snippet.
- Edit specific snippet.
- Multi add sound files.

### Backend
- Fix failing tests!
- Improve caching.
- REST service is not using a good scheme for naming the routes. In fact
the way it is now written is considered an anti pattern.
- Controller might benefit from a rewrite into using a command pattern. Will
make new controller functionality easier to implement.
