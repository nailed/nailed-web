(function(window, $) {

    function NailedCore(win, jQ) {
        this.logger = new win.NailedLogger();
        this.networkManager = new win.NetworkManager(this);
        this.userInfo = {"authenticated":false};

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
            if(packet.success){
                packet.userData.authenticated = true;
                this.setUserInfo(packet.userData);
            }else{
                alert("Wrong email/password");
            }
        }else if(packet instanceof PacketCloseConnection){

        }
    }

    window.Nailed = new NailedCore(window, $);

    $("body").on("click", "#signInButton", function(){
        window.Nailed.networkManager.connectToServer(function(handler){
            handler.sendPacket(new PacketAuthenticate($("input#authEmail").val(), $("input#authPassword").val()));
        });
    });

    $("body").on("click", "#signOutButton", function(){
        $("input#authEmail").val("");
        $("input#authPassword").val("");
        window.Nailed.networkManager.disconnect("Logout");
        window.Nailed.setUserInfo({"authenticated":false});
    });

})(window, jQuery);