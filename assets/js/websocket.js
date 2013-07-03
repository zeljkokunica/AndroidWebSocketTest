(function() {
		// window object
		var global = window;

		// WebSocket Object. All listener methods are cleaned up!
		var WebSocket = global.WebSocket = function(url) {
			// get a new websocket object from factory (check com.strumsoft.websocket.WebSocketFactory.java)
			this.socket = WebSocketFactory.getInstance(url);
			// store in registry
			if(this.socket) {
				WebSocket.store[this.socket.getId()] = this;
			} else {
				throw new Error('Websocket instantiation failed! Address might be wrong.');
			}
			return this;
		};

		// storage to hold websocket object for later invokation of event methods
		WebSocket.store = {};

		// static event methods to call event methods on target websocket objects
		WebSocket.onmessage = function (evt) {
			var func = WebSocket.store[evt._target]['onmessage'];
			if (func) { 
				func.call(global ,evt);
			}
		}	

		WebSocket.onopen = function (evt) {
			var func = WebSocket.store[evt._target]['onopen'];
			if (func) { 
				func.call(global ,evt);
			}
		}

		WebSocket.onclose = function (evt) {
			var func = WebSocket.store[evt._target]['onclose'];
			if (func) { 
				func.call(global ,evt);
			}
		}

		WebSocket.onerror = function (evt) {
			var func = WebSocket.store[evt._target]['onerror'];
			if (func) { 
				func.call(global ,evt);
			}
		}
		
		WebSocket.onreadystatechanged = function (evt) {
			WebSocket.store[evt._target]['readyState'] = evt.data;
		}

		// instance event methods
		WebSocket.prototype.send = function(data) {
			this.socket.send(data);
		}

		WebSocket.prototype.close = function() {
			this.socket.close();
		}
	})();