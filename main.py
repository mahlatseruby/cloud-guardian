from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from supabase import create_client, Client
import os
from dotenv import load_dotenv
import resend
from datetime import datetime

load_dotenv()

app = FastAPI(title="Cloud Guardian")