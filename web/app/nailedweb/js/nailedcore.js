(function(window, $) {

    function NailedCore(win, jQ) {
        this.logger = new win.NailedLogger();
        this.networkManager = new win.NetworkManager(this);
        this.userInfo = {"fullName":""};

        win.NetworkManager = undefined;
        win.NailedLogger = undefined;
    }

    NailedCore.prototype.setUserInfo = function(data){
        if(data == null) throw new Error("Data is null");
        this.userInfo = data;
        this.getScope().$apply(function($scope){
            $scope.userInfo = data;
        });
    }

    NailedCore.prototype.getScope = function(){
        return angular.element("body").scope();
    }

    NailedCore.prototype.version = "0.1-SNAPSHOT";
    NailedCore.prototype.$ = $;
    NailedCore.prototype.BOOTED = false;

    NailedCore.prototype.handleFatal = function(error){
        //TODO: implement
        this.logger.error("Fatal error: " + error);
    }

    NailedCore.prototype.handlePacket = function(packet){
        if(packet instanceof PacketKeepAlive){
            this.networkManager.sendPacket(packet);
        }else if(packet instanceof PacketAuthenticationSuccess){
            this.setUserInfo(packet.userInfo)
        }else if(packet instanceof PacketCloseConnection){

        }
    }

    window.Nailed = new NailedCore(window, $);

})(window, jQuery);