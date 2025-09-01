//
//  main.swift
//  GetUTI
//
//  Created by Icloud Bringardner on 8/30/25.
//
import ApplicationServices
import Foundation
import UniformTypeIdentifiers

var fileExtension: String = "txt"

print("Hello, World!")
guard let utype = UTType(filenameExtension: fileExtension) else {
    exit(3)
  }
print(utype.preferredMIMEType)
print(utype.preferredFilenameExtension)
print(utype.identifier)
print(utype.description)
CFString uti = "txt"
var ary = LSCopyAllRoleHandlersForContentType(uti);
