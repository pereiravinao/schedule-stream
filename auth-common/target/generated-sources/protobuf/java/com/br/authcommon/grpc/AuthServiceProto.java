// Generated by the protocol buffer compiler.  DO NOT EDIT!
// NO CHECKED-IN PROTOBUF GENCODE
// source: auth.proto
// Protobuf Java Version: 4.30.2

package com.br.authcommon.grpc;

public final class AuthServiceProto {
  private AuthServiceProto() {}
  static {
    com.google.protobuf.RuntimeVersion.validateProtobufGencodeVersion(
      com.google.protobuf.RuntimeVersion.RuntimeDomain.PUBLIC,
      /* major= */ 4,
      /* minor= */ 30,
      /* patch= */ 2,
      /* suffix= */ "",
      AuthServiceProto.class.getName());
  }
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_authcommon_TokenRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_authcommon_TokenRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_authcommon_UserResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_authcommon_UserResponse_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\nauth.proto\022\nauthcommon\"\035\n\014TokenRequest" +
      "\022\r\n\005token\030\001 \001(\t\"T\n\014UserResponse\022\020\n\010usern" +
      "ame\030\001 \001(\t\022\r\n\005email\030\002 \001(\t\022\024\n\014phone_number" +
      "\030\003 \001(\t\022\r\n\005roles\030\004 \003(\t2\226\001\n\013AuthService\022C\n" +
      "\rValidateToken\022\030.authcommon.TokenRequest" +
      "\032\030.authcommon.UserResponse\022B\n\014RefreshTok" +
      "en\022\030.authcommon.TokenRequest\032\030.authcommo" +
      "n.UserResponseB,\n\026com.br.authcommon.grpc" +
      "B\020AuthServiceProtoP\001b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_authcommon_TokenRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_authcommon_TokenRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_authcommon_TokenRequest_descriptor,
        new java.lang.String[] { "Token", });
    internal_static_authcommon_UserResponse_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_authcommon_UserResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_authcommon_UserResponse_descriptor,
        new java.lang.String[] { "Username", "Email", "PhoneNumber", "Roles", });
    descriptor.resolveAllFeaturesImmutable();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
