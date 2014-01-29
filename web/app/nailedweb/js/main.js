Nailed.logger.info("Initializing Nailed-Web...");
Nailed.logger.info("Version: " + Nailed.version);
Nailed.logger.info("---------------------");

Nailed.logger.info("Initializing websocket connection");
Nailed.networkManager.setWebsocketUrl(document.location.origin.replace("http://","ws://").replace("https://","wss://") + "/websocket/");
//Nailed.networkManager.connectToServer();