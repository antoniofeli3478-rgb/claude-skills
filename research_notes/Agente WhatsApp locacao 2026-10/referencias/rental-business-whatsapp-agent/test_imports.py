def main():
    from app import config
    from app.database import Base
    from app.agent import AGENT
    from app.api import app
    print("Import check: OK")
    print(f"Tables: {len(Base.metadata.tables)}")
    print(f"FastAPI routes: {len(app.routes)}")

if __name__ == "__main__":
    main()
