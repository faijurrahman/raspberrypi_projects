import uinput
import time
import RPi.GPIO as GPIO
 
GPIO.setmode(GPIO.BCM)

# Up, Down, left, right, fire
GPIO.setup(17, GPIO.IN)
GPIO.setup(27, GPIO.IN)
GPIO.setup(22, GPIO.IN)
GPIO.setup(23, GPIO.IN)
GPIO.setup(4, GPIO.IN)
GPIO.setup(18, GPIO.IN)
GPIO.setup(24, GPIO.IN)
GPIO.setup(25, GPIO.IN)
 
events = (uinput.BTN_A, uinput.BTN_B, uinput.BTN_X, uinput.BTN_Y, uinput.ABS_X + (0, 255, 0, 0), uinput.ABS_Y + (0, 255, 0, 0)) 
#events = (uinput.BTN_JOYSTICK, uinput.ABS_X + (0, 255, 0, 0), uinput.ABS_Y + (0, 255, 0, 0))

device = uinput.Device(events)
 
# Bools to keep track of movement
A = False
B = False
X = False
Y = False
up = False
down = False
left = False
right = False
 
# Center joystick
# syn=False to emit an "atomic" (128, 128) event.
device.emit(uinput.ABS_X, 128, syn=False)
device.emit(uinput.ABS_Y, 128)
 
while True:
 if (not A) and (not GPIO.input(4)): # Fire button pressed  
	A = True
	device.emit(uinput.BTN_A, 1)  
 if A and GPIO.input(4): # Fire button released  
	A = False
	device.emit(uinput.BTN_A, 0) 
 if (not B) and (not GPIO.input(18)): # Fire button pressed  
	B = True
	device.emit(uinput.BTN_B, 1)  
 if B and GPIO.input(18): # Fire button released  
	B = False
	device.emit(uinput.BTN_B, 0) 
 if (not X) and (not GPIO.input(24)): # Fire button pressed  
	X = True
	device.emit(uinput.BTN_X, 1)  
 if X and GPIO.input(24): # Fire button released  
	X = False
	device.emit(uinput.BTN_X, 0) 
 if (not Y) and (not GPIO.input(25)): # Fire button pressed  
 	Y = True
 	device.emit(uinput.BTN_Y, 1)  
 if Y and GPIO.input(25): # Fire button released  
	Y = False
	device.emit(uinput.BTN_Y, 0) 
 
 if (not up) and (not GPIO.input(17)): # Up button pressed
  up = True
  device.emit(uinput.ABS_Y, 0) # Zero Y
 if up and GPIO.input(17): # Up button released
  up = False
  device.emit(uinput.ABS_Y, 128) # Center Y
 if (not down) and (not GPIO.input(27)): # Down button pressed
  down = True
  device.emit(uinput.ABS_Y, 255) # Max Y
 if down and GPIO.input(27): # Down button released
  down = False
  device.emit(uinput.ABS_Y, 128) # Center Y
 if (not left) and (not GPIO.input(22)): # Left button pressed
  left = True
  device.emit(uinput.ABS_X, 0) # Zero X
 if left and GPIO.input(22): # Left button released
  left = False
  device.emit(uinput.ABS_X, 128) # Center X
 if (not right) and (not GPIO.input(23)):# Right button pressed
  right = True
  device.emit(uinput.ABS_X, 255) # Max X
 if right and GPIO.input(23): # Right button released
  right = False
  device.emit(uinput.ABS_X, 128) # Center X
 time.sleep(0.2) # Poll every 40ms (otherwise CPU load gets too high)
