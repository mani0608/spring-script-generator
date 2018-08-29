const {app, BrowserWindow} = require('electron')
const path = require('path')
const url = require('url')
const logger = require('electron-log');
const getPort = require('get-port');

const isDev = require('electron-is-dev');

const jar = 'script-generator-0.1.0.jar';
let serverProcess;
let serverPort;

function startServer (port) {
	const platform = process.platform;
	
	 const server = isDev ? `script-generator-server/build/libs/${jar}` 
    : `${path.join(app.getAppPath(), '..', '..', jar)}`;
  logger.info(`Launching server with jar ${server} at port ${port}...`);

  serverProcess = require('child_process')
    .spawn('java', [ '-jar', server, `--server.port=${port}`]);

  serverProcess.stdout.on('data', data => {
    logger.info('SERVER: ' + data);
  });

  serverProcess.stderr.on('data', data => {
    logger.error('SERVER: ' + data);
  });

  if (serverProcess.pid) {
    logger.info("Server PID: " + serverProcess.pid);
    serverPort = port; // save the port
  } else {
    logger.error("Failed to launch server process.")
  }
}

// Keep a global reference of the window object, if you don't, the window will
// be closed automatically when the JavaScript object is garbage collected.
let mainWin;

function createWindow () {
  // Create the browser window.
  mainWin = new BrowserWindow({width: 800, height: 600})

  // and load the index.html of the app.
  mainWin.loadURL(url.format({
    pathname: path.join(__dirname, 'splash.html'),
    protocol: 'file:',
    slashes: true
  }))
  
   // check server health and switch to main page
  const axios = require('axios');
   setTimeout(function cycle() {
     axios.get(`http://localhost:${serverPort}`)
       .then(response => {
		console.log("*****LOADING INDEX PAGE FROM SPRING***********");
        mainWin.loadURL(`http://localhost:${serverPort}`);
       })
       .catch(e => {
        console.log(e);
         setTimeout(cycle, 1000);
       });
   }, 200);

  // Open the DevTools.
  mainWin.webContents.openDevTools()

  // Emitted when the window is closed.
  mainWin.on('closed', () => {
    // Dereference the window object, usually you would store windows
    // in an array if your app supports multi windows, this is the time
    // when you should delete the corresponding element.
    mainWin = null
  })
}

function initialize() {
  // Create window first to show splash before starting server
  createWindow();
  // Start server at an available port (prefer 8080)
  getPort({ port: 8080 }).then(port => {
    startServer(port);
  })
}

// This method will be called when Electron has finished
// initialization and is ready to create browser windows.
// Some APIs can only be used after this event occurs.
app.on('ready', initialize)

// Quit when all windows are closed.
app.on('window-all-closed', () => {
  // On macOS it is common for applications and their menu bar
  // to stay active until the user quits explicitly with Cmd + Q
  if (process.platform !== 'darwin') {
    app.quit()
  }
})

app.on('activate', () => {
  // On macOS it's common to re-create a window in the app when the
  // dock icon is clicked and there are no other windows open.
  if (win === null) {
    createWindow()
  }
})

app.on('will-quit', () => {
  if (serverProcess) {
    logger.info(`Killing server process ${serverProcess.pid}`);
    const kill = require('tree-kill');
    kill(serverProcess.pid, 'SIGTERM', function (err) {
      logger.info('Server process killed');
        serverProcess = null;
    });
  }
});

// In this file you can include the rest of your app's specific main process
// code. You can also put them in separate files and require them here.