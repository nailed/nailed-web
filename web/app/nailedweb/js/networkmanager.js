(function(window, $) {

    function getParameterByName(name) {
        name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
        var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
            results = regex.exec(location.search);
        return results == null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
    }

    window.NetworkManager = function(nx) {
        this.packets = {};
        this.Nailed = nx;

        this.registerPackets();
    }

    window.NetworkManager.prototype.registerPacket = function(id, name, packet){
        packet.prototype.PACKETID = id;
        packet.prototype.PACKETNAME = name;
        this.packets[id] = packet;
        window[name] = packet;
    }

    window.NetworkManager.prototype.websocketUrl = "";
    window.NetworkManager.prototype.setWebsocketUrl = function(url){
        this.websocketUrl = url;
    }
    window.NetworkManager.prototype.connectToServer = function() {
        var self = this;
        this.websocket = new WebSocket(this.websocketUrl);
        this.websocket.onopen = function(){
            self.Nailed.logger.info("Connected to the server!");
            self.sendPacket(new PacketAuthenticate(getParameterByName("secret")));
        }
        this.websocket.onclose = function(){
            self.Nailed.logger.info("Server connection closed!");
        }
        this.websocket.onmessage = function(d){
            var data = JSON.parse(d.data);
            var packetId = data.id;
            if(self.packets[packetId] === "undefined") return;
            var packet = new self.packets[packetId]();
            if(data.data !== "undefined"){
                packet.read(data.data);
            }
            Nailed.handlePacket(packet);
            self.Nailed.logger.info("Received: " + d.data);
        }
        this.websocket.onerror = function(){
            self.Nailed.handleFatal("Server connection failed!");
        }
    }

    window.NetworkManager.prototype.sendPacket = function(packet){
        if(packet == null || packet == undefined) return;
        var pdata = {};
        packet.write(pdata);
        var sendingData = JSON.stringify({"id": packet.PACKETID, "data": pdata});
        this.websocket.send(sendingData);
        this.Nailed.logger.info("Sent: " + sendingData);
    }

    window.NetworkManager.prototype.registerPackets = function(){
        this.registerPacket(0x0, "PacketKeepAlive", function(){
            this.read = function(data){
                this.randomId = data.randomId;
            }
            this.write = function(data){
                data.randomId = this.randomId;
            }
        });
        this.registerPacket(0x1, "PacketAuthenticate", function(_secret){
            if(_secret !== undefined) this.secret = _secret;
            this.write = function(data){
                data.secret = this.secret;
            }
        });
        this.registerPacket(0x2, "PacketAuthenticationSuccess", function(_secret){
            this.read = function(data){
                this.userInfo = data.userInfo
            }
        });
        this.registerPacket(0x3, "PacketCloseConnection", function(){
            this.read = function(data){
                this.reason = data.reason;
            }
        });
    }

})(window, jQuery);
