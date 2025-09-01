//
//  UTIWrapper.hpp
//  GetLaunchData
//
//  Created by Icloud Bringardner on 8/31/25.
//

#pragma once
#include <string>

class UTIWrapper {
public:
    // Returns the UTI string for a given file extension (e.g., "jpg" → "public.jpeg")
    static std::string getUTIFromExtension(const std::string& extension);
};
