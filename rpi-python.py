from bluetooth import *

'''

This code would be used for controlling GPIO ports,
in this case just two LEDs at port 21 and 26

# import RPi.GPIO as GPIO
LED0 = 21
LED1 = 26

GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)
GPIO.setup(LED0,GPIO.OUT)
GPIO.output(LED0,0)
GPIO.setup(LED1,GPIO.OUT)
GPIO.output(LED1,0)

'''
server_socket = BluetoothSocket(RFCOMM)

server_socket.bind(("",PORT_ANY))
server_socket.listen(1)
server_socket.settimeout(None)

client_socket,address = server_socket.accept()
print("Accepted connection from " + str(address))

while(1):
    data = client_socket.recv(1024)
    print("Received data: " + str(data.decode()))
    
    client_socket.send("OK") # Can check on Android side
    
    int_data = None
    try:
        int_data = int(data)
    except ValueError:
        print("Received a non numeric input.")
        continue
    
    '''
    A valid int_data would display the last two bits
    of the binary representation of the number sent
    by lighting up the appropriate LEDs

    #GPIO.output(LED0,int_data&1)
    #GPIO.output(LED1,int_data&2)
    '''

'''
Note that this connection times out on its own when idle
'''
client_socket.close()
server_socket.close()