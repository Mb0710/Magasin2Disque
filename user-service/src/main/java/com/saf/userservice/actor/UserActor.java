package com.saf.userservice.actor;

import com.saf.core.Actor;
import com.saf.core.ActorContext;
import com.saf.core.ActorRef;
import com.saf.core.Message;
import com.saf.userservice.actor.messages.UserMessages;
import com.saf.userservice.model.User;
import com.saf.userservice.repository.UserRepository;
import com.saf.userservice.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;
import java.time.LocalDateTime;

public class UserActor implements Actor {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ActorRef emailActor;

    public UserActor(UserRepository userRepository, PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil, ActorRef emailActor) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailActor = emailActor;
    }

    @Override
    public void onReceive(Message message, ActorContext context) {
        Object payload = message.getPayload();

        if (payload instanceof UserMessages.RegisterUser) {
            handleRegister((UserMessages.RegisterUser) payload, message);
        } else if (payload instanceof UserMessages.Login) {
            handleLogin((UserMessages.Login) payload, message);
        } else if (payload instanceof UserMessages.VerifyEmail) {
            handleVerifyEmail((UserMessages.VerifyEmail) payload, message);
        } else if (payload instanceof UserMessages.ResendVerification) {
            handleResend((UserMessages.ResendVerification) payload, message);
        } else if (payload instanceof UserMessages.GetUserById) {
            handleGetUserById((UserMessages.GetUserById) payload, message);
        }
    }

    private void handleRegister(UserMessages.RegisterUser msg, Message originalMessage) {
        try {
            if (userRepository.existsByEmail(msg.email())) {
                originalMessage.reply(new UserMessages.UserOperationError("Email déjà utilisé"));
                return;
            }

            User user = new User();
            user.setUsername(msg.username());
            user.setEmail(msg.email());
            user.setPassword(passwordEncoder.encode(msg.password()));
            user.setEmailVerified(false);
            user.setVerificationToken(UUID.randomUUID().toString());
            user.setTokenExpiryDate(LocalDateTime.now().plusHours(24));
            user.setRole("USER");

            user = userRepository.save(user);

            // Envoyer email de vérification
            String verificationLink = "http://localhost:8081/api/auth/verify?token=" + user.getVerificationToken();
            emailActor.send(
                    new com.saf.userservice.actor.EmailActor.SendVerificationEmail(user.getEmail(), user.getUsername(),
                            user.getVerificationToken()),
                    originalMessage.getSender());

            originalMessage.reply(new UserMessages.UserRegistered(user.getId(), user.getUsername(), true));
        } catch (Exception e) {
            originalMessage.reply(new UserMessages.UserOperationError("Erreur: " + e.getMessage()));
        }
    }

    private void handleLogin(UserMessages.Login msg, Message originalMessage) {
        try {
            Optional<User> userOpt = userRepository.findByUsername(msg.username());

            if (userOpt.isEmpty()) {
                originalMessage
                        .reply(new UserMessages.UserOperationError("Nom d'utilisateur ou mot de passe incorrect"));
                return;
            }

            User user = userOpt.get();

            if (!passwordEncoder.matches(msg.password(), user.getPassword())) {
                originalMessage
                        .reply(new UserMessages.UserOperationError("Nom d'utilisateur ou mot de passe incorrect"));
                return;
            }

            if (!user.isEmailVerified()) {
                originalMessage.reply(new UserMessages.UserOperationError("Veuillez vérifier votre email"));
                return;
            }

            String token = jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole());
            originalMessage
                    .reply(new UserMessages.LoginSuccess(token, user.getId(), user.getUsername(), user.getRole()));
        } catch (Exception e) {
            originalMessage.reply(new UserMessages.UserOperationError("Erreur: " + e.getMessage()));
        }
    }

    private void handleVerifyEmail(UserMessages.VerifyEmail msg, Message originalMessage) {
        try {
            Optional<User> userOpt = userRepository.findByVerificationToken(msg.token());

            if (userOpt.isEmpty()) {
                originalMessage.reply(new UserMessages.EmailVerified(false));
                return;
            }

            User user = userOpt.get();

            if (user.getTokenExpiryDate() != null && user.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
                originalMessage.reply(new UserMessages.EmailVerified(false));
                return;
            }

            user.setEmailVerified(true);
            user.setVerificationToken(null);
            user.setTokenExpiryDate(null);
            userRepository.save(user);

            originalMessage.reply(new UserMessages.EmailVerified(true));
        } catch (Exception e) {
            originalMessage.reply(new UserMessages.EmailVerified(false));
        }
    }

    private void handleResend(UserMessages.ResendVerification msg, Message originalMessage) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(msg.email());

            if (userOpt.isEmpty()) {
                originalMessage.reply(new UserMessages.UserOperationError("Email introuvable"));
                return;
            }

            User user = userOpt.get();

            if (user.isEmailVerified()) {
                originalMessage.reply(new UserMessages.UserOperationError("Email déjà vérifié"));
                return;
            }

            user.setVerificationToken(UUID.randomUUID().toString());
            user.setTokenExpiryDate(LocalDateTime.now().plusHours(24));
            userRepository.save(user);

            emailActor.send(
                    new com.saf.userservice.actor.EmailActor.SendVerificationEmail(user.getEmail(), user.getUsername(),
                            user.getVerificationToken()),
                    originalMessage.getSender());

            originalMessage.reply(new UserMessages.UserOperationError("Email renvoyé"));
        } catch (Exception e) {
            originalMessage.reply(new UserMessages.UserOperationError("Erreur: " + e.getMessage()));
        }
    }

    private void handleGetUserById(UserMessages.GetUserById msg, Message originalMessage) {
        try {
            Optional<User> userOpt = userRepository.findById(msg.userId());
            originalMessage.reply(new UserMessages.GetUserByIdResponse(userOpt.orElse(null)));
        } catch (Exception e) {
            originalMessage.reply(new UserMessages.GetUserByIdResponse(null));
        }
    }
}
