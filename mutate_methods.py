import multiprocessing as mp
import subprocess
import os
import sys
import shutil
import time


MUTATOR="target/cug-1.0-SNAPSHOT.jar"
NUM_THREADS=1
MUTATION_LEVEL = sys.argv[2]
REMOVE="rm"
# REMOVE="del"
TIME_TO_TIMEOUT = 300

mypath = sys.argv[1]

def mutate_file(filename:str):

	path = os.path.join(mypath, filename).replace("\\", "/")
	p = subprocess.Popen(["java", "-cp", MUTATOR, "Main", "--mutate_methods", "--file", path, "--mutation_level", MUTATION_LEVEL, "--num_threads", str(NUM_THREADS)])
	time.sleep(TIME_TO_TIMEOUT)
	poll = p.poll()
	if poll is None:
		print("TIMEOUT: ",path)
		p.kill()
		shutil.move(path, os.path.join("timeout", filename))
		

if __name__ == "__main__":

    if not os.path.exists("timeout"):
        os.makedirs("timeout")
    
    alldirs = [ f for f in os.listdir(mypath) if os.path.isfile(os.path.join(mypath, f))]

    pool = mp.Pool(processes=1)#mp.cpu_count())
    pool.map(mutate_file, alldirs)