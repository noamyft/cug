import multiprocessing as mp
import subprocess
import os
import sys
import shutil
import time
from threading import Timer
import signal


MUTATOR="target/cug-1.0-SNAPSHOT.jar"
NUM_THREADS=1
MUTATION_LEVEL = sys.argv[2]
REMOVE="rm"
# REMOVE="del"
TIME_TO_TIMEOUT = 2

mypath = sys.argv[1]

def mutate_file(filename:str):

	def timer_and_kill(p):
		poll = p.poll()
		if poll is None:
			print("TIMEOUT: ",path)
			os.kill(p.pid, signal.SIGKILL)
			shutil.move(path, os.path.join("timeout", filename))
	
	path = os.path.join(mypath, filename).replace("\\", "/")
	p = subprocess.Popen(["java", "-cp", MUTATOR, "Main", "--mutate_methods", "--file", path, "--mutation_level", MUTATION_LEVEL, "--num_threads", str(NUM_THREADS)])
	timer = Timer(TIME_TO_TIMEOUT, timer_and_kill, [p])
	
	try:
		timer.start()
		stdout, stderr = p.communicate()
	finally:
		timer.cancel()
	
		

if __name__ == "__main__":

    if not os.path.exists("timeout"):
        os.makedirs("timeout")
    
    alldirs = [ f for f in os.listdir(mypath) if os.path.isfile(os.path.join(mypath, f))]

    pool = mp.Pool(processes=mp.cpu_count())
    pool.map(mutate_file, alldirs)