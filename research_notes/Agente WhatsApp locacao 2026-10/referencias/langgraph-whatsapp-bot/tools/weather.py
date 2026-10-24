import random
from langchain_core.tools import tool

@tool
def get_current_weather(location: str) -> str:
    """
    Get the current weather information for a given location.
    
    Args:
        location: The city or location to get weather for (e.g., "New York", "London")
        
    Returns:
        str: Current weather information including temperature, conditions, etc.
    """
    
    # Sample weather conditions
    conditions = ["sunny", "cloudy", "rainy", "partly cloudy", "overcast"]
    temperatures = list(range(15, 35))  # Temperature range in Celsius
    
    condition = random.choice(conditions)
    temp = random.choice(temperatures)
    humidity = random.randint(30, 90)
    
    return f"Current weather in {location}: {temp}°C, {condition}, humidity: {humidity}%"